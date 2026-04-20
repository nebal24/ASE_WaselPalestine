package com.wasel.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${jwt.secret-key}")
    private String SECRET_KEY;

    // Access token: 1 day  
    private static final long ACCESS_TOKEN_EXPIRY_MS = 1000L * 60 * 60 * 24;
    // Refresh token: 7 days
    private static final long REFRESH_TOKEN_EXPIRY_MS = 1000L * 60 * 60 * 24 * 7;

    private static final String CLAIM_TOKEN_TYPE = "tokenType";
    private static final String TYPE_ACCESS = "access";
    private static final String TYPE_REFRESH = "refresh";

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /** Generates a short-lived access token (15 min). */
    public String generateAccessToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        if (userDetails instanceof com.wasel.entity.User user) {
            claims.put("userId", user.getId());
            claims.put("role", user.getRole().name());
        }
        claims.put(CLAIM_TOKEN_TYPE, TYPE_ACCESS);
        return buildToken(claims, userDetails, ACCESS_TOKEN_EXPIRY_MS);
    }

    /** Generates a long-lived refresh token (7 days). */
    public String generateRefreshToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(CLAIM_TOKEN_TYPE, TYPE_REFRESH);
        return buildToken(claims, userDetails, REFRESH_TOKEN_EXPIRY_MS);
    }

    public boolean isAccessToken(String token) {
        return TYPE_ACCESS.equals(extractClaim(token, c -> c.get(CLAIM_TOKEN_TYPE, String.class)));
    }

    public boolean isRefreshToken(String token) {
        return TYPE_REFRESH.equals(extractClaim(token, c -> c.get(CLAIM_TOKEN_TYPE, String.class)));
    }


    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }

    private String buildToken(Map<String, Object> extraClaims, UserDetails userDetails, long expiryMs) {
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiryMs))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignInKey() {
        byte[] keyBytes = SECRET_KEY.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
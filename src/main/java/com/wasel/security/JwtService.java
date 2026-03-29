package com.wasel.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Service class for handling JWT (JSON Web Token) operations
 * Responsible for token generation, validation, and claim extraction
 */
@Service
public class JwtService {

    /**
     * Secret key used for signing JWTs
     * Must be kept secure - in production, this should be in environment variables
     * Current key is 256-bit (32 bytes) for HS256 algorithm
     */
    private static final String SECRET_KEY = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";

    /**
     * Extracts username (email) from JWT token
     *
     * @param token the JWT token
     * @return username (email) stored in token subject
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Generic method to extract any claim from token
     *
     * @param token the JWT token
     * @param claimsResolver function to extract specific claim
     * @return the extracted claim value
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Generates JWT token for authenticated user
     *
     * @param userDetails the authenticated user
     * @return JWT token string
     */
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> extraClaims = new HashMap<>();
        if (userDetails instanceof com.wasel.entity.User user) {
            extraClaims.put("userId", user.getId());
            extraClaims.put("role", user.getRole().name());
        }
        return generateToken(extraClaims, userDetails);    }

    /**
     * Generates JWT token with additional claims
     *
     * @param extraClaims additional claims to include in token
     * @param userDetails the authenticated user
     * @return JWT token string with 24 hour expiration
     */
    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return Jwts
                .builder()
                .setClaims(extraClaims)                          // Add extra claims
                .setSubject(userDetails.getUsername())           // Set user email as subject
                .setIssuedAt(new Date(System.currentTimeMillis())) // Token creation time
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24)) // 24 hours
                .signWith(getSignInKey(), SignatureAlgorithm.HS256) // Sign with HS256
                .compact();
    }

    /**
     * Validates if token is valid for given user
     *
     * @param token the JWT token
     * @param userDetails the user to validate against
     * @return true if token is valid and not expired
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    /**
     * Checks if token has expired
     *
     * @param token the JWT token
     * @return true if token expired
     */
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Extracts expiration date from token
     *
     * @param token the JWT token
     * @return expiration date
     */
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Extracts all claims from token by parsing and verifying signature
     *
     * @param token the JWT token
     * @return all claims in the token
     */
    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())    // Set signing key for verification
                .build()
                .parseClaimsJws(token)            // Parse and verify token
                .getBody();                         // Get claims
    }

    /**
     * Gets signing key for JWT operations
     * Converts secret key string to Key object suitable for HS256
     *
     * @return signing key
     */
    private Key getSignInKey() {
        byte[] keyBytes = SECRET_KEY.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }

}
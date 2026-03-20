package com.wasel.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import jakarta.annotation.Nonnull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import com.wasel.entity.User;

import java.io.IOException;

/**
 * JWT authentication filter that intercepts each request once
 * Validates JWT token and sets up Spring Security context
 * Extends OncePerRequestFilter to ensure single execution per request
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    // Service for JWT token operations (extract, validate)
    private final JwtService jwtService;

    // Spring Security service to load user details from database
    private final UserDetailsService userDetailsService;

    /**
     * Intercepts each HTTP request to validate JWT token
     *
     * @param request the HTTP request
     * @param response the HTTP response
     * @param filterChain the filter chain for continuing request processing
     * @throws ServletException if servlet error occurs
     * @throws IOException if I/O error occurs
     */
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        // 1. Extract Authorization header
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;

        // 2. Check if token exists and has correct format
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            // No token found - continue filter chain (will be rejected by security)
            filterChain.doFilter(request, response);
            return;
        }

        // 3. Extract JWT token (remove "Bearer " prefix)
        jwt = authHeader.substring(7);

        // 4. Extract username (email) from token
        userEmail = jwtService.extractUsername(jwt);

        // 5. If email extracted and user not already authenticated
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // 6. Load user details from database
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

            // 7. Validate the token
            if (jwtService.isTokenValid(jwt, userDetails)) {

                // 8. Create authentication token for Spring Security
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

                // 9. Add request details to authentication token
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                // 10. Set authentication in SecurityContext
                SecurityContextHolder.getContext().setAuthentication(authToken);

                // 11. Add userId as request attribute for controllers to use
                request.setAttribute("userId", ((User) userDetails).getId());
            }
        }

        // 12. Continue with the filter chain
        filterChain.doFilter(request, response);
    }
}
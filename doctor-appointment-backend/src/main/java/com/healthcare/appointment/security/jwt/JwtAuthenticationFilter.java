package com.healthcare.appointment.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * Filter that intercepts every HTTP request to check for a valid JWT token.
 *
 * <p>Flow:
 * 1. Checks if the {@code Authorization} header exists and starts with {@code Bearer }.
 * 2. Extracts the token and decodes the username (email).
 * 3. If the user is not yet authenticated in the current {@code SecurityContext}:
 *    - Loads the {@code UserDetails} from the database.
 *    - Validates the token against the user details.
 *    - Creates an {@code Authentication} token and sets it in the Context.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        
        log.info("====================================================");
        log.info("JWT FILTER START");
        log.info("URI={}", request.getRequestURI());
        
        final String authHeader = request.getHeader("Authorization");
        log.info("Authorization={}", authHeader);
        
        final String jwt;
        final String userEmail;

        // 1. Check for Bearer token
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.info("JWT FILTER END - No Bearer Token");
            log.info("====================================================");
            filterChain.doFilter(request, response);
            return; // Skip filter if no token
        }

        jwt = authHeader.substring(7);
        
        try {
            // 2. Extract username from token
            userEmail = jwtService.extractUsername(jwt);
            log.info("Username={}", userEmail);
            
            // 3. Validate and set SecurityContext
            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
                
                boolean isValid = jwtService.isTokenValid(jwt, userDetails);
                log.info("Token Valid={}", isValid);
                
                if (isValid) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    
                    log.info("Authentication Before setAuthentication={}", SecurityContextHolder.getContext().getAuthentication());
                    // The user is now authenticated for this specific request thread
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    log.info("Authentication After setAuthentication={}", SecurityContextHolder.getContext().getAuthentication());
                }
            }
        } catch (Exception e) {
            log.info("JWT Validation Exception: {}", e.getMessage());
            // Token is malformed, expired, or invalid.
            // We intentionally swallow the exception here. By not setting the SecurityContext,
            // Spring Security will eventually reject the request and trigger our JwtAuthenticationEntryPoint.
        }
        
        log.info("Authentication BEFORE filterChain.doFilter={}", SecurityContextHolder.getContext().getAuthentication());
        // Continue down the filter chain
        filterChain.doFilter(request, response);
        log.info("Authentication AFTER filterChain.doFilter={}", SecurityContextHolder.getContext().getAuthentication());
        log.info("JWT FILTER END");
        log.info("====================================================");
    }
}

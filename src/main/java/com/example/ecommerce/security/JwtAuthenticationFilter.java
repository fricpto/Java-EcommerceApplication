package com.example.ecommerce.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.lang.NonNull;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.ecommerce.repository.BlacklistedTokenRepository;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private final BlacklistedTokenRepository blacklistRepo;

    public JwtAuthenticationFilter(BlacklistedTokenRepository blacklistRepo) {
        this.blacklistRepo = blacklistRepo;
        logger.info("JwtAuthenticationFilter bean created");
    }

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        // Skip JWT processing for static/frontend assets and probes
        String path = request.getRequestURI();
        if (path.startsWith("/assets/") || path.equals("/vite.svg") || path.equals("/favicon.ico")
                || path.startsWith("/css/") || path.startsWith("/js/") || path.startsWith("/images/")
                || path.startsWith("/webjars/") || path.startsWith("/.well-known/")
                || "OPTIONS".equals(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }
        logger.info("JwtAuthenticationFilter invoked for URI: {}", request.getRequestURI());
        // existing debug logs...

        final String authHeader = request.getHeader("Authorization");
        String username = null;
        String jwt = null;

        // Extract token from header
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwt = authHeader.substring(7);
            if (blacklistRepo.existsByToken(jwt)) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token is blacklisted");
            }
            username = jwtUtil.extractUsername(jwt);
        }

        // Validate token and set authentication
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            logger.debug("Authorization header: {}", authHeader);
            // after extracting jwt
            logger.debug("Extracted JWT: {}", jwt);
            logger.debug("Extracted username: {}", username);
            logger.debug("Token valid: {}", jwtUtil.validateToken(jwt, userDetails));
            if (jwtUtil.validateToken(jwt, userDetails)) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);
    }
}
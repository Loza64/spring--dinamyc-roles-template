package com.server.app.filters;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.server.app.config.JsonWebToken;
import com.server.app.entities.User;
import com.server.app.services.impl.UserService;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JsonWebToken jwtUtil;
    private final UserService userService;

    public JwtAuthenticationFilter(@Lazy JsonWebToken jwtUtil, UserService userService) {
        this.jwtUtil = jwtUtil;
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String token = authHeader.substring(7);

        if (jwtUtil.isTokenExpired(token)) {
            filterChain.doFilter(request, response);
            return;
        }

        Claims claims = jwtUtil.extracClaims(token);
        if (claims == null) {
            filterChain.doFilter(request, response);
            return;
        }

        Integer userId = jwtUtil.extractIdUser(token);
        if (userId == null) {
            filterChain.doFilter(request, response);
            return;
        }

        User user = userService.findById(userId);
        if (user == null) {
            filterChain.doFilter(request, response);
            return;
        }

        Set<GrantedAuthority> authorities = new HashSet<>();

        authorities.addAll(
                user.getRoles().stream()
                        .flatMap(role -> role.getPermissions().stream()
                                .map(permission -> new SimpleGrantedAuthority(
                                        permission.getMethod() + ":" + permission.getPath())))
                        .collect(Collectors.toSet()));

        user.getRoles().forEach(role -> authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName())));

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user, null,
                authorities);

        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request, response);
    }
}

package com.server.app.filters;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class DynamicAuthorizationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            String method = request.getMethod();
            String path = request.getRequestURI();

            boolean allowed = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals(method + ":" + path) ||
                            a.getAuthority().equals("ROLE_ADMIN"));

            if (!allowed) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\": \"Acceso denegado: no tienes permisos para esta ruta\"}");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}

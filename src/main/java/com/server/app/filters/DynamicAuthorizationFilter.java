package com.server.app.filters;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.server.app.dto.response.ExceptionResponse;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class DynamicAuthorizationFilter extends OncePerRequestFilter {

    private final AntPathMatcher pathMatcher = new AntPathMatcher();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication != null && authentication.isAuthenticated()) {
                String method = request.getMethod();
                String path = request.getRequestURI();

                if (!isAuthorized(authentication, method, path)) {
                    sendError(response, HttpServletResponse.SC_FORBIDDEN,
                            "Acceso denegado: no tienes permisos para esta ruta: " + path);
                    return;
                }
            }

            filterChain.doFilter(request, response);
        } catch (Exception ex) {
            sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Error interno en el filtro de autorización");
        }
    }

    private boolean isAuthorized(Authentication authentication, String method, String path) {
        return authentication.getAuthorities().stream().anyMatch(a -> {
            String authority = a.getAuthority();

            String[] parts = authority.split(":", 2);
            if (parts.length != 2)
                return false;

            String authMethod = parts[0];
            String authPath = parts[1];

            return method.equals(authMethod) && pathMatcher.match(authPath, path);
        });
    }

    private void sendError(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");

        ExceptionResponse error = new ExceptionResponse(status, message);
        response.getWriter().write(objectMapper.writeValueAsString(error));
    }
}

package com.server.app.filters;

import java.io.IOException;
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
import com.server.app.services.UserService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
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

    /**
     * Omitir el filtro para los endpoints de login y signup
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        return path.equals("/api/auth/login") || path.equals("/api/auth/signup");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            sendError(response, HttpServletResponse.SC_UNAUTHORIZED,
                    "Encabezado Authorization ausente o formato inválido. Se esperaba 'Bearer <token>'");
            return;
        }

        final String token = authHeader.substring(7);

        try {
            if (jwtUtil.isTokenExpired(token)) {
                sendError(response, HttpServletResponse.SC_UNAUTHORIZED, "El token ha expirado");
                return;
            }

            Claims claims = jwtUtil.extracClaims(token);
            if (claims == null) {
                sendError(response, HttpServletResponse.SC_UNAUTHORIZED, "No se pudieron obtener los claims del token");
                return;
            }

            Integer userId = jwtUtil.extractIdUser(token);
            if (userId == null) {
                sendError(response, HttpServletResponse.SC_UNAUTHORIZED,
                        "El token no contiene un ID de usuario válido");
                return;
            }

            User user = userService.findById(userId);
            if (user == null) {
                sendError(response, HttpServletResponse.SC_UNAUTHORIZED, "El usuario asociado al token no existe");
                return;
            }

            Set<GrantedAuthority> authorities = user.getRole().getPermissions().stream()
                    .map(permission -> new SimpleGrantedAuthority(permission.getMethod() + ":" + permission.getPath()))
                    .collect(Collectors.toSet());

            authorities.add(new SimpleGrantedAuthority("ROLE_" + user.getRole().getName()));

            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user, null,
                    authorities);
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (ExpiredJwtException e) {
            sendError(response, HttpServletResponse.SC_UNAUTHORIZED, "El token ha expirado");
            return;

        } catch (JwtException e) {
            sendError(response, HttpServletResponse.SC_UNAUTHORIZED, "Token inválido o manipulado");
            return;

        } catch (Exception e) {
            sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error interno en la autenticación");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private void sendError(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.getWriter().write(String.format("{\"error\": \"%s\"}", message));
    }
}

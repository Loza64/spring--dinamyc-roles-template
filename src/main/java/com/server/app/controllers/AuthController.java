package com.server.app.controllers;

import com.server.app.dto.auth.LoginDto;
import com.server.app.dto.auth.UpdatePasswordDto;
import com.server.app.dto.response.AuthResponse;
import com.server.app.dto.user.UserCreateDto;
import com.server.app.entities.User;
import com.server.app.services.UserService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginDto body) {
        AuthResponse response = userService.login(body.getUsername(), body.getPassword());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> signUp(@RequestBody UserCreateDto body) {
        AuthResponse response = userService.signUp(body);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/profile")
    public ResponseEntity<User> getProfile(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.startsWith("Bearer ")
                ? authHeader.substring(7)
                : authHeader;

        User user = userService.profile(token);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/update/password")
    public ResponseEntity<User> updatePassword(@RequestHeader("Authorization") String authHeader,
            @RequestBody UpdatePasswordDto dto) {
        String token = authHeader.startsWith("Bearer ") ? authHeader.substring(7) : authHeader;

        User user = userService.updatePassword(token, dto);
        return ResponseEntity.ok(user);
    }
}
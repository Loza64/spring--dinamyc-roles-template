package com.server.app.controllers;

import com.server.app.dto.user.UserCreateDto;
import com.server.app.dto.user.UserUpdateDto;
import com.server.app.entities.User;
import com.server.app.services.impl.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<User> createUser(@Valid @RequestBody UserCreateDto dto) {
        return ResponseEntity.ok(userService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable int id, @Valid @RequestBody UserUpdateDto dto) {
        return ResponseEntity.ok(userService.updateUser(id, dto));
    }

    @GetMapping
    public ResponseEntity<List<User>> findAll() {
        return ResponseEntity.ok(userService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> findById(@PathVariable int id) {
        return ResponseEntity.ok(userService.findById(id));
    }

    @PutMapping("/{id}/role")
    public ResponseEntity<User> assignRole(
            @PathVariable int id,
            @Valid @RequestBody RoleIdRequest roleRequest) {
        return ResponseEntity.ok(userService.assignRole(id, roleRequest.getRoleId()));
    }

    @PutMapping("/{id}/password")
    public ResponseEntity<User> updatePassword(
            @PathVariable int id,
            @RequestBody @NotNull String newPassword) {
        return ResponseEntity.ok(userService.updatePassword(id, newPassword));
    }

    public static class RoleIdRequest {
        @NotNull(message = "El roleId es obligatorio")
        @Positive(message = "El roleId debe ser un número positivo")
        private Long roleId;

        public Long getRoleId() {
            return roleId;
        }

        public void setRoleId(Long roleId) {
            this.roleId = roleId;
        }
    }
}

package com.server.app.controllers;

import com.server.app.dto.user.UserCreateDto;
import com.server.app.dto.user.UserUpdateDto;
import com.server.app.entities.User;
import com.server.app.services.impl.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // 🔹 CREAR USUARIO
    @PostMapping
    public ResponseEntity<User> createUser(@Valid @RequestBody UserCreateDto dto) {
        return ResponseEntity.ok(userService.create(dto));
    }

    // 🔹 ACTUALIZAR USUARIO
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable int id, @Valid @RequestBody UserUpdateDto dto) {
        return ResponseEntity.ok(userService.updateUser(id, dto));
    }

    // 🔹 OBTENER TODOS LOS USUARIOS
    @GetMapping
    public ResponseEntity<List<User>> findAll() {
        return ResponseEntity.ok(userService.findAll());
    }

    // 🔹 OBTENER USUARIO POR ID
    @GetMapping("/{id}")
    public ResponseEntity<User> findById(@PathVariable int id) {
        return ResponseEntity.ok(userService.findById(id));
    }

    // 🔹 ASIGNAR ROLES
    @PutMapping("/{id}/roles")
    public ResponseEntity<User> assignRoles(@PathVariable int id, @RequestBody Set<Long> roleIds) {
        return ResponseEntity.ok(userService.assignRoles(id, roleIds));
    }

    // 🔹 ACTUALIZAR CONTRASEÑA
    @PutMapping("/{id}/password")
    public ResponseEntity<User> updatePassword(@PathVariable int id, @RequestBody String newPassword) {
        return ResponseEntity.ok(userService.updatePassword(id, newPassword));
    }
}

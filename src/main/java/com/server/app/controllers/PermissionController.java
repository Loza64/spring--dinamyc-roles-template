package com.server.app.controllers;

import com.server.app.dto.permission.PermissionDto;
import com.server.app.services.impl.PermissionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/permissions")
public class PermissionController {

    private final PermissionService permissionService;

    public PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @PostMapping
    public ResponseEntity<PermissionDto> save(@Valid @RequestBody PermissionDto dto) {
        return ResponseEntity.ok(permissionService.save(dto));
    }

    @GetMapping
    public ResponseEntity<List<PermissionDto>> findAll() {
        return ResponseEntity.ok(permissionService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PermissionDto> findById(@PathVariable Long id) {
        return permissionService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        permissionService.delete(id);
        return ResponseEntity.ok("Permiso eliminado");
    }
}

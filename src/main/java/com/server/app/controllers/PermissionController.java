package com.server.app.controllers;

import com.server.app.dto.response.Pagination;
import com.server.app.entities.Permission;
import com.server.app.services.PermissionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/permissions")
public class PermissionController {

    private final PermissionService permissionService;

    public PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @GetMapping
    public ResponseEntity<Pagination<Permission>> findAll(
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        var permissionsPage = permissionService.findAll(page, size);
        var response = new Pagination<Permission>(
                permissionsPage.getContent(),
                permissionsPage.getNumber(),
                permissionsPage.getSize(),
                permissionsPage.getTotalPages(),
                permissionsPage.getTotalElements());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Permission> findById(@PathVariable Long id) {
        return permissionService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

}

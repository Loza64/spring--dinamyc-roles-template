package com.server.app.controllers;

import com.server.app.dto.role.RoleDto;
import com.server.app.services.impl.RoleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/roles")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @PostMapping
    public ResponseEntity<RoleDto> save(@Valid @RequestBody RoleDto dto) {
        return ResponseEntity.ok(roleService.save(dto));
    }

    @GetMapping
    public ResponseEntity<List<RoleDto>> findAll() {
        return ResponseEntity.ok(roleService.findAll());
    }

    @PutMapping("/{id}/permissions")
    public ResponseEntity<RoleDto> assignPermissions(@PathVariable Long id, @RequestBody Set<Long> permissionIds) {
        return ResponseEntity.ok(roleService.assignPermissions(id, permissionIds));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        roleService.delete(id);
        return ResponseEntity.ok("Rol eliminado");
    }
}

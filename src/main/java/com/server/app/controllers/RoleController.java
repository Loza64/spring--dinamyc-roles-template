package com.server.app.controllers;

import com.server.app.dto.response.Pagination;
import com.server.app.dto.role.RoleDto;
import com.server.app.entities.Role;
import com.server.app.services.RoleService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/roles")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @PostMapping
    public ResponseEntity<Role> save(@Valid @RequestBody RoleDto role) {
        return ResponseEntity.ok(roleService.save(role));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Role> update(@PathVariable Long id, @Valid @RequestBody RoleDto dto) {
        Role updatedRole = roleService.update(id, dto);
        return ResponseEntity.ok(updatedRole);
    }

    @GetMapping
    public ResponseEntity<Pagination<Role>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<Role> rolesPage = roleService.findAll(page, size);

        Pagination<Role> response = new Pagination<Role>(
                rolesPage.getContent(),
                rolesPage.getNumber(),
                rolesPage.getSize(),
                rolesPage.getTotalPages(),
                rolesPage.getTotalElements());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Role> findById(@PathVariable Long id) {
        return roleService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        roleService.delete(id);
        return ResponseEntity.ok("Rol eliminado");
    }
}

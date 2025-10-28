package com.server.app.services.impl;

import com.server.app.dto.role.RoleDto;
import com.server.app.entities.Permission;
import com.server.app.entities.Role;
import com.server.app.repositories.PermissionRepository;
import com.server.app.repositories.RoleRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RoleService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    public RoleService(RoleRepository roleRepository, PermissionRepository permissionRepository) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
    }

    public List<RoleDto> findAll() {
        return roleRepository.findAll()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public Optional<RoleDto> findById(Long id) {
        return roleRepository.findById(id).map(this::toDto);
    }

    public RoleDto save(RoleDto dto) {
        Role role = new Role();
        if (dto.getId() != null) {
            role = roleRepository.findById(dto.getId()).orElse(new Role());
        }

        role.setName(dto.getName());

        if (dto.getPermissionIds() != null && !dto.getPermissionIds().isEmpty()) {
            Set<Permission> permissions = new HashSet<>(permissionRepository.findAllById(dto.getPermissionIds()));
            role.setPermissions(permissions);
        }

        Role saved = roleRepository.save(role);
        return toDto(saved);
    }

    public void delete(Long id) {
        roleRepository.deleteById(id);
    }

    public RoleDto assignPermissions(Long roleId, Set<Long> permissionIds) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found"));

        Set<Permission> permissions = new HashSet<>(permissionRepository.findAllById(permissionIds));
        role.setPermissions(permissions);

        Role saved = roleRepository.save(role);
        return toDto(saved);
    }

    private RoleDto toDto(Role role) {
        RoleDto dto = new RoleDto();
        dto.setId(role.getId());
        dto.setName(role.getName());
        if (role.getPermissions() != null) {
            Set<Long> permIds = role.getPermissions().stream()
                    .map(Permission::getId)
                    .collect(Collectors.toSet());
            dto.setPermissionIds(permIds);
        }
        return dto;
    }
}

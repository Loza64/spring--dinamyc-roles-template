package com.server.app.services.impl;

import com.server.app.dto.permission.PermissionDto;
import com.server.app.entities.Permission;
import com.server.app.repositories.PermissionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PermissionService {

    private final PermissionRepository permissionRepository;

    public PermissionService(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    // 🔹 OBTENER TODOS LOS PERMISOS (devuelve DTOs)
    public List<PermissionDto> findAll() {
        return permissionRepository.findAll()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    // 🔹 OBTENER PERMISO POR ID
    public Optional<PermissionDto> findById(Long id) {
        return permissionRepository.findById(id).map(this::toDto);
    }

    // 🔹 CREAR O ACTUALIZAR PERMISO DESDE DTO
    public PermissionDto save(PermissionDto dto) {
        Permission permission = new Permission();
        permission.setId(dto.getId());
        permission.setPath(dto.getPath());
        permission.setMethod(dto.getMethod());

        Permission saved = permissionRepository.save(permission);
        return toDto(saved);
    }

    // 🔹 ELIMINAR PERMISO
    public void delete(Long id) {
        permissionRepository.deleteById(id);
    }

    // 🔹 CONVERSIÓN ENTIDAD → DTO
    private PermissionDto toDto(Permission permission) {
        PermissionDto dto = new PermissionDto();
        dto.setId(permission.getId());
        dto.setPath(permission.getPath());
        dto.setMethod(permission.getMethod());
        return dto;
    }
}

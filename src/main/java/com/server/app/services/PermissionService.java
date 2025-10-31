package com.server.app.services;

import com.server.app.entities.Permission;
import com.server.app.repositories.PermissionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class PermissionService {

    private final PermissionRepository permissionRepository;

    public PermissionService(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    @Transactional
    public Page<Permission> findAll(int page, int size) {
        return permissionRepository.findAll(PageRequest.of(page, size));
    }

    @Transactional
    public Optional<Permission> findById(Long id) {
        return permissionRepository.findById(id);
    }

    @Transactional
    public void createIfNotExists(String path, String method) {
        Optional<Permission> existing = permissionRepository.findByPathAndMethod(path, method);
        if (existing.isEmpty()) {
            Permission permission = new Permission();
            permission.setPath(path);
            permission.setMethod(method);
            permissionRepository.save(permission);
        }
    }
}

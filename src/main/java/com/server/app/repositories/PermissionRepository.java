package com.server.app.repositories;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.server.app.entities.Permission;

@EnableJpaRepositories
public interface PermissionRepository extends JpaRepository<Permission, Long> {
    Page<Permission> findAll(Pageable pageable);

    Optional<Permission> findByPathAndMethod(String path, String method);
}

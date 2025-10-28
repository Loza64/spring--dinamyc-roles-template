package com.server.app.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.server.app.entities.Permission;

@EnableJpaRepositories
public interface PermissionRepository extends JpaRepository<Permission, Long> {

}

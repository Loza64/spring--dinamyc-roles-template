package com.server.app.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table(name = "permissions", uniqueConstraints = @UniqueConstraint(columnNames = { "path", "method" }))
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Permission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = false)
    private String path;

    @Column(nullable = false)
    private String method;

    @Column(nullable = true)
    private String title;
}
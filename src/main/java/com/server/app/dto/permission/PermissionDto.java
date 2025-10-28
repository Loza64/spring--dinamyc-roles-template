package com.server.app.dto.permission;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PermissionDto {

    private Long id;

    @NotBlank(message = "El path es obligatorio")
    @Size(max = 255, message = "El path no puede superar 255 caracteres")
    private String path;

    @NotBlank(message = "El método HTTP es obligatorio")
    @Size(max = 10, message = "El método HTTP no puede superar 10 caracteres")
    private String method; // GET, POST, PUT, DELETE
}

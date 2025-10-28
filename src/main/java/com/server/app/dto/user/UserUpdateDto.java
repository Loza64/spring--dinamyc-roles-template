package com.server.app.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Set;

@Data
public class UserUpdateDto {

    @Size(max = 100, message = "El nombre no puede superar 100 caracteres")
    private String name;

    @Size(max = 100, message = "El apellido no puede superar 100 caracteres")
    private String surname;

    @Email(message = "Email no válido")
    private String email;

    private Set<Long> roleIds;
}

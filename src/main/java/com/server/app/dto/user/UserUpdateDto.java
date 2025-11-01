package com.server.app.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserUpdateDto {
    @Size(min = 3, max = 20, message = "El nombre de usuario debe tener entre 3 y 20 caracteres")
    @Pattern(regexp = "^[A-Za-zÁÉÍÓÚáéíóúÑñ\\.\\s]+$", message = "El nombre de usuario solo puede contener letras, espacios y puntos")
    private String username;

    @Size(min = 2, max = 50, message = "El nombre debe tener entre 2 y 50 caracteres")
    @Pattern(regexp = "^[A-Za-zÁÉÍÓÚáéíóúÑñ\\s]+$", message = "El nombre solo puede contener letras y espacios")
    private String name;

    @Size(min = 2, max = 50, message = "El apellido debe tener entre 2 y 50 caracteres")
    @Pattern(regexp = "^[A-Za-zÁÉÍÓÚáéíóúÑñ\\s]+$", message = "El apellido solo puede contener letras y espacios")
    private String surname;

    @Email(message = "Debe proporcionar un correo electrónico válido")
    private String email;

    @Positive(message = "El roleId debe ser un número positivo")
    private Long role;
}

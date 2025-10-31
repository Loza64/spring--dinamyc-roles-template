package com.server.app.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.server.app.config.JsonWebToken;
import com.server.app.dto.auth.UpdatePasswordDto;
import com.server.app.dto.response.AuthResponse;
import com.server.app.dto.user.UserCreateDto;
import com.server.app.dto.user.UserUpdateDto;
import com.server.app.entities.Role;
import com.server.app.entities.User;
import com.server.app.repositories.RoleRepository;
import com.server.app.repositories.UserRepository;

@Service
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final JsonWebToken jwt;
    private final RoleRepository roleRepository;

    public UserService(PasswordEncoder passwordEncoder, UserRepository userRepository, JsonWebToken jwt,
            RoleRepository roleRepository) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.jwt = jwt;
        this.roleRepository = roleRepository;
    }

    public AuthResponse login(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Contraseña incorrecta");
        }

        String token = jwt.createToken(user);
        return new AuthResponse(token, user);
    }

    public AuthResponse signUp(UserCreateDto dto) {
        if (userRepository.findByUsername(dto.getUsername()).isPresent()) {
            throw new RuntimeException("El username ya está en uso");
        }

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setName(dto.getName());
        user.setSurname(dto.getSurname());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));

        Role defaultRole = roleRepository.findById(2L)
                .orElseThrow(() -> new RuntimeException("Rol por defecto no encontrado"));
        user.setRole(defaultRole);

        userRepository.save(user);

        String token = jwt.createToken(user);
        return new AuthResponse(token, user);
    }

    public User profile(String token) {
        int id = jwt.extractIdUser(token);
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    public User create(UserCreateDto dto) {
        if (userRepository.findByUsername(dto.getUsername()).isPresent()) {
            throw new RuntimeException("El username ya está en uso");
        }

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setName(dto.getName());
        user.setSurname(dto.getSurname());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));

        if (dto.getRole() != null) {
            Role role = roleRepository.findById(dto.getRole())
                    .orElseThrow(() -> new RuntimeException("Rol no encontrado"));
            user.setRole(role);
        }

        return userRepository.save(user);
    }

    public Page<User> findAll(int page, int size) {
        return userRepository.findAll(PageRequest.of(page, size));
    }

    public User findById(int id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    public User updatePassword(String token, UpdatePasswordDto dto) {
        int id = jwt.extractIdUser(token);
        User user = findById(id);

        if (!passwordEncoder.matches(dto.getOldpassword(), user.getPassword())) {
            throw new IllegalArgumentException("La contraseña actual es incorrecta");
        }

        if (passwordEncoder.matches(dto.getNewpassword(), user.getPassword())) {
            throw new IllegalArgumentException("La nueva contraseña no puede ser igual a la anterior");
        }

        if (!dto.getNewpassword().equals(dto.getConfirmpassword())) {
            throw new IllegalArgumentException("Las contraseñas nuevas no coinciden");
        }

        user.setPassword(passwordEncoder.encode(dto.getNewpassword()));
        return userRepository.save(user);
    }

    public User updateUser(int userId, UserUpdateDto dto) {
        User user = findById(userId);

        if (dto.getName() != null)
            user.setName(dto.getName());
        if (dto.getSurname() != null)
            user.setSurname(dto.getSurname());
        if (dto.getEmail() != null)
            user.setEmail(dto.getEmail());

        if (dto.getRole() != null) {
            Role role = roleRepository.findById(dto.getRole())
                    .orElseThrow(() -> new RuntimeException("Rol no encontrado"));
            user.setRole(role);
        }

        return userRepository.save(user);
    }
}

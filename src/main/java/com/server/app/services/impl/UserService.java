package com.server.app.services.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.server.app.config.JsonWebToken;
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

    // 🔹 LOGIN
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

        // 🔹 Asignar rol por defecto (ID 1)
        Role defaultRole = roleRepository.findById(2L)
                .orElseThrow(() -> new RuntimeException("Rol por defecto no encontrado"));

        user.setRoles(Set.of(defaultRole));

        userRepository.save(user);

        String token = jwt.createToken(user);
        return new AuthResponse(token, user);
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

        return userRepository.save(user);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User findById(int id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    public User assignRoles(int userId, Set<Long> roleIds) {
        User user = findById(userId);
        Set<Role> roles = new HashSet<>(roleRepository.findAllById(roleIds));
        user.setRoles(roles);
        return userRepository.save(user);
    }

    public User updatePassword(int userId, String newPassword) {
        User user = findById(userId);
        user.setPassword(passwordEncoder.encode(newPassword));
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

        // Actualizar roles si vienen
        if (dto.getRoleIds() != null && !dto.getRoleIds().isEmpty()) {
            Set<Role> roles = new HashSet<>(roleRepository.findAllById(dto.getRoleIds()));
            user.setRoles(roles);
        }

        return userRepository.save(user);
    }
}

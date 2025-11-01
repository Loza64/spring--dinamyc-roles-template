package com.server.app.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.server.app.config.JsonWebToken;
import com.server.app.dto.auth.UpdatePasswordDto;
import com.server.app.dto.response.AuthResponse;
import com.server.app.dto.user.UpdateProfileDto;
import com.server.app.dto.user.UserCreateDto;
import com.server.app.dto.user.UserUpdateDto;
import com.server.app.entities.Role;
import com.server.app.entities.User;
import com.server.app.exceptions.ServerException;
import com.server.app.exceptions.BadRequestException;
import com.server.app.exceptions.ForbiddenException;
import com.server.app.exceptions.NotFoundException;
import com.server.app.exceptions.UnauthorizedException;
import com.server.app.repositories.RoleRepository;
import com.server.app.repositories.UserRepository;

@Service
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final JsonWebToken jwt;
    private final RoleRepository roleRepository;

    public UserService(PasswordEncoder passwordEncoder, UserRepository userRepository,
            JsonWebToken jwt, RoleRepository roleRepository) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.jwt = jwt;
        this.roleRepository = roleRepository;
    }

    public AuthResponse login(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UnauthorizedException("Usuario no encontrado"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new UnauthorizedException("Contraseña incorrecta");
        }

        String token = jwt.createToken(user);
        return new AuthResponse(token, user);
    }

    @Transactional
    public AuthResponse signUp(UserCreateDto dto) {
        validateUniqueUsername(dto.getUsername(), null);
        validateUniqueEmail(dto.getEmail(), null);
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
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado"));
    }

    @Transactional
    public User create(UserCreateDto dto) {
        validateUniqueUsername(dto.getUsername(), null);
        validateUniqueEmail(dto.getEmail(), null);
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
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado"));
    }

    @Transactional
    public AuthResponse updateProfile(String token, UpdateProfileDto dto) {
        int userId = jwt.extractIdUser(token);
        User existingUser = findById(userId);

        validateUniqueUsername(dto.getUsername(), userId);
        validateUniqueEmail(dto.getEmail(), userId);

        existingUser.setUsername(dto.getUsername());
        existingUser.setName(dto.getName());
        existingUser.setSurname(dto.getSurname());
        existingUser.setEmail(dto.getEmail());

        User updatedUser = userRepository.save(existingUser);
        return new AuthResponse(token, updatedUser);
    }

    @Transactional
    public User updatePassword(String token, UpdatePasswordDto dto) {
        int id = jwt.extractIdUser(token);
        User user = findById(id);

        if (!passwordEncoder.matches(dto.getOldpassword(), user.getPassword())) {
            throw new ForbiddenException("La contraseña actual es incorrecta");
        }

        if (passwordEncoder.matches(dto.getNewpassword(), user.getPassword())) {
            throw new BadRequestException("La nueva contraseña no puede ser igual a la anterior");
        }

        if (!dto.getNewpassword().equals(dto.getConfirmpassword())) {
            throw new BadRequestException("Las contraseñas nuevas no coinciden");
        }

        user.setPassword(passwordEncoder.encode(dto.getNewpassword()));
        return userRepository.save(user);
    }

    @Transactional
    public User updateUser(int userId, UserUpdateDto dto) {
        User user = findById(userId);

        if (dto.getUsername() != null && !dto.getUsername().isBlank()) {
            validateUniqueUsername(dto.getUsername(), userId);
            user.setUsername(dto.getUsername());
        }

        if (dto.getName() != null && !dto.getName().isBlank()) {
            user.setName(dto.getName());
        }

        if (dto.getSurname() != null && !dto.getSurname().isBlank()) {
            user.setSurname(dto.getSurname());
        }

        if (dto.getEmail() != null && !dto.getEmail().isBlank()) {
            validateUniqueEmail(dto.getEmail(), userId);
            user.setEmail(dto.getEmail());
        }

        if (dto.getRole() != null) {
            Role role = roleRepository.findById(dto.getRole())
                    .orElseThrow(() -> new NotFoundException("Rol no encontrado"));
            user.setRole(role);
        }

        return userRepository.save(user);
    }

    private void validateUniqueUsername(String username, Integer currentUserId) {
        userRepository.findByUsername(username).ifPresent(existing -> {
            if (currentUserId == null || existing.getId() != currentUserId) {
                throw new ServerException("El nombre de usuario ya está en uso");
            }
        });
    }

    private void validateUniqueEmail(String email, Integer currentUserId) {
        userRepository.findByEmail(email).ifPresent(existing -> {
            if (currentUserId == null || existing.getId() != currentUserId) {
                throw new ServerException("El correo electrónico ya está en uso");
            }
        });
    }
}

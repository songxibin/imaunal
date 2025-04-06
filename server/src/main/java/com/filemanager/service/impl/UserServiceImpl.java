package com.filemanager.service.impl;

import com.filemanager.model.User;
import com.filemanager.model.dto.UserDTO;
import com.filemanager.repository.UserRepository;
import com.filemanager.security.JwtService;
import com.filemanager.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.debug("Loading user by username: {}", username);
        return userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    logger.error("User not found: {}", username);
                    return new UsernameNotFoundException("User not found: " + username);
                });
    }

    @Override
    @Transactional
    public UserDTO register(User user) {
        logger.info("Registering new user: {}", user.getUsername());
        
        if (userRepository.existsByUsername(user.getUsername())) {
            logger.error("Username already exists: {}", user.getUsername());
            throw new RuntimeException("Username already exists");
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            logger.error("Email already exists: {}", user.getEmail());
            throw new RuntimeException("Email already exists");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoles(List.of("ROLE_USER"));
        User savedUser = userRepository.save(user);
        logger.info("User registered successfully: {}", savedUser.getUsername());
        return convertToDTO(savedUser);
    }

    @Override
    public UserDTO authenticate(User userRequest) {
        logger.info("Authenticating user: {}", userRequest.getUsername());
        String username = userRequest.getUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    logger.error("User not found: {}", username);
                    return new UsernameNotFoundException("User not found: " + username);
                });
        
        logger.debug("User found: {}", user.getUsername());
        String jwtToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        logger.info("Authentication successful for user: {}", username);
        
        return convertToDTO(user, jwtToken, refreshToken);
    }

    @Override
    public UserDTO getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        logger.debug("Getting current user: {}", username);
        
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    logger.error("User not found: {}", username);
                    return new UsernameNotFoundException("User not found: " + username);
                });

        logger.debug("Current user found: {}", user.getUsername());
        return convertToDTO(user);
    }

    @Override
    @Transactional
    public UserDTO updateUser(Long id, User userRequest) {
        logger.info("Updating user with ID: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("User not found with ID: {}", id);
                    return new RuntimeException("User not found");
                });

        if (userRequest.getEmail() != null) {
            logger.debug("Updating email from '{}' to '{}'", user.getEmail(), userRequest.getEmail());
            user.setEmail(userRequest.getEmail());
        }
        if (userRequest.getFullName() != null) {
            logger.debug("Updating fullName from '{}' to '{}'", user.getFullName(), userRequest.getFullName());
            user.setFullName(userRequest.getFullName());
        }

        User updatedUser = userRepository.save(user);
        logger.info("User updated successfully: {}", updatedUser.getUsername());
        return convertToDTO(updatedUser);
    }

    @Override
    @Transactional
    public void changePassword(Long id, String oldPassword, String newPassword) {
        logger.info("Changing password for user with ID: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("User not found with ID: {}", id);
                    return new RuntimeException("User not found");
                });

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            logger.error("Invalid old password for user: {}", user.getUsername());
            throw new RuntimeException("Invalid old password");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        logger.info("Password changed successfully for user: {}", user.getUsername());
    }

    private UserDTO convertToDTO(User user) {
        logger.trace("Converting User to DTO: {}", user.getUsername());
        UserDTO dto = new UserDTO();
        dto.setUserid(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setFullName(user.getFullName());
        dto.setRoles(user.getRoles());
        dto.setCreatedAt(user.getCreatedAt());
        logger.trace("User converted to DTO successfully");
        return dto;
    }

    private UserDTO convertToDTO(User user, String token, String refreshToken) {
        logger.trace("Converting User to DTO with tokens: {}", user.getUsername());
        UserDTO dto = convertToDTO(user);
        dto.setToken(token);
        dto.setRefresh_token(refreshToken);
        logger.trace("User converted to DTO with tokens successfully");
        return dto;
    }
} 
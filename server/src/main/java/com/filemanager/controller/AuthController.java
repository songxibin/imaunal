package com.filemanager.controller;

import com.filemanager.model.User;
import com.filemanager.model.dto.UserDTO;
import com.filemanager.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserDTO> register(@RequestBody User user) {
        logger.info("Registering new user: {}", user.getUsername());
        try {
            UserDTO registeredUser = userService.register(user);
            logger.info("User registered successfully: {}", registeredUser.getUsername());
            return ResponseEntity.ok(registeredUser);
        } catch (Exception e) {
            logger.error("Failed to register user: {}", user.getUsername(), e);
            throw e;
        }
    }

    @PostMapping("/login")
    public ResponseEntity<UserDTO> login(@RequestBody User user) {
        logger.info("User login attempt: {}", user.getUsername());
        try {
            UserDTO userResp = userService.authenticate(user);
            logger.info("User logged in successfully: {}", userResp.getUsername());
            return ResponseEntity.ok(userResp);
        } catch (Exception e) {
            logger.error("Login failed for user: {}", user.getUsername(), e);
            throw e;
        }
    }
} 
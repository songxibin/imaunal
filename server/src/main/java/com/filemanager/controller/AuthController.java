package com.filemanager.controller;

import com.filemanager.model.User;
import com.filemanager.model.dto.UserDTO;
import com.filemanager.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserDTO> register(@RequestBody User user) {
        return ResponseEntity.ok(userService.register(user));
    }

    @PostMapping("/login")
    public ResponseEntity<UserDTO> login(@RequestBody User user) {
        // Note: Actual login is handled by Spring Security
        System.out.println(user.getUsername());
        UserDTO userResp;
        try{
            userResp = userService.authenticate(user);
        } catch (Exception e) {
            System.err.println(e);
            throw new RuntimeException(e);
        }
        return ResponseEntity.ok(userResp);
    }
} 
package com.filemanager.model.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class UserDTO {
    private Long userid;
    private String token;
    private String refresh_token;
    private String username;
    private String email;
    private String fullName;
    private List<String> roles;
    private LocalDateTime createdAt;
} 
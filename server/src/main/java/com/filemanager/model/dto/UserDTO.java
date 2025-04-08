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
    private String subscriptionType;
    
    // Add getter and setter
    public String getSubscriptionType() {
        return subscriptionType;
    }
    
    public void setSubscriptionType(String subscriptionType) {
        this.subscriptionType = subscriptionType;
    }
}
package com.filemanager.service;

import com.filemanager.model.User;
import com.filemanager.model.dto.UserDTO;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
    UserDTO register(User user);
    UserDTO authenticate(User userRequest);
    UserDTO getCurrentUser();
    UserDTO updateUser(Long id, User user);
    void changePassword(Long id, String oldPassword, String newPassword);
} 
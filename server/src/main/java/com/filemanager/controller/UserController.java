package com.filemanager.controller;

import com.filemanager.model.User;
import com.filemanager.model.dto.UserDTO;
import com.filemanager.model.dto.UserStatsDTO;
import com.filemanager.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/current")
    public ResponseEntity<UserDTO> getCurrentUser() {
        return ResponseEntity.ok(userService.getCurrentUser());
    }

    @PutMapping("/current")
    public ResponseEntity<UserDTO> updateUser(@RequestBody User user) {
        return ResponseEntity.ok(userService.updateUser(userService.getCurrentUser().getUserid(), user));
    }

    @PutMapping("/current/password")
    public ResponseEntity<Void> changePassword(@RequestParam String oldPassword, @RequestParam String newPassword) {
        userService.changePassword(userService.getCurrentUser().getUserid(), oldPassword, newPassword);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{userId}/stats")
    public ResponseEntity<UserStatsDTO> getUserStats(@PathVariable Long userId) {
            return ResponseEntity.ok(userService.getUserStats(userId));
    }
}
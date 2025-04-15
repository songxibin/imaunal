package com.filemanager.service.impl;

import com.filemanager.exception.ResourceNotFoundException;
import com.filemanager.model.User;
import com.filemanager.model.UserStats;
import com.filemanager.model.dto.UserDTO;
import com.filemanager.model.dto.UserStatsDTO;
import com.filemanager.repository.DocumentRepository;
import com.filemanager.repository.UserRepository;
import com.filemanager.repository.UserStatsRepository;
import com.filemanager.security.JwtService;
import com.filemanager.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.DecimalFormat;
import java.util.List;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtService jwtService;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Autowired
    private UserStatsRepository userStatsRepository;
    @Autowired
    private DocumentRepository documentRepository;

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
        dto.setSubscriptionType(user.getSubscriptionType());  // Add this line
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

    public UserStatsDTO getUserStats(Long userId) {
        log.debug("Getting stats for user: {}", userId);

        UserStats userStats = userStatsRepository.findByUserId(userId);
        if(userStats == null)
            throw new ResourceNotFoundException("User stats not found");

        log.info("Stats retrieved for user {}: storage={}, languages={}, words={}", 
            userId, userStats.getStorageUsedFormatted(), userStats.getLanguageCount(), userStats.getTotalWordCount());

        return convertToUserStatusDTO(userStats);
    }

    private String formatFileSize(Long bytes) {
        if (bytes == null) return "0 B";
        
        final String[] units = new String[] { "B", "KB", "MB", "GB", "TB" };
        int digitGroups = (int) (Math.log10(bytes) / Math.log10(1024));
        return new DecimalFormat("#,##0.#")
                .format(bytes / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

    private Long getStorageLimitBySubscription(String subscriptionType) {
        // 根据订阅类型返回存储限制（单位：字节）
        return switch (subscriptionType) {
            case "BASIC" -> 10L * 1024 * 1024 * 1024;     // 10GB
            case "PRO" -> 50L * 1024 * 1024 * 1024;       // 50GB
            case "ENTERPRISE" -> 1024L * 1024 * 1024 * 1024; // 1TB
            default -> 5L * 1024 * 1024 * 1024;           // 5GB for free users
        };
    }


    @Override
    @Transactional
    public UserDTO updateSubscription(Long userId, String subscriptionType) {
        logger.info("Updating subscription for user ID: {} to {}", userId, subscriptionType);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.error("User not found with ID: {}", userId);
                    return new ResourceNotFoundException("User not found");
                });

        // Validate subscription type
        if (!isValidSubscriptionType(subscriptionType)) {
            logger.error("Invalid subscription type: {}", subscriptionType);
            throw new IllegalArgumentException("Invalid subscription type");
        }

        user.setSubscriptionType(subscriptionType.toUpperCase());
        User updatedUser = userRepository.save(user);
        logger.info("Subscription updated successfully for user: {}", updatedUser.getUsername());
        
        return convertToDTO(updatedUser);
    }

    private boolean isValidSubscriptionType(String subscriptionType) {
        if (subscriptionType == null) return false;
        String upperType = subscriptionType.toUpperCase();
        return upperType.equals("FREE") || 
               upperType.equals("BASIC") || 
               upperType.equals("PRO") || 
               upperType.equals("ENTERPRISE");
    }


    private UserStatsDTO convertToUserStatusDTO(UserStats user) {
        logger.trace("Converting User to DTO: {}", user.getId());
        UserStatsDTO dto = new UserStatsDTO();
        dto.setUserId(user.getId());
        dto.setLanguageCount(user.getLanguageCount());
        dto.setTotalWordCount(user.getTotalWordCount());
        dto.setStorageUsed(user.getStorageUsed());
        dto.setStorageLimit(user.getStorageLimit());
        dto.setStorageUsagePercent(user.getStorageUsagePercent());
        dto.setStorageUsedFormatted(user.getStorageUsedFormatted());
        dto.setStorageLimitFormatted(user.getStorageLimitFormatted());

        logger.trace("UserStats converted to UserStatsDTO successfully");
        return dto;
    }
}
package com.filemanager.service.impl;

import com.filemanager.exception.ResourceNotFoundException;
import com.filemanager.model.User;
import com.filemanager.model.UserStats;
import com.filemanager.repository.UserRepository;
import com.filemanager.repository.UserStatsRepository;
import com.filemanager.service.UserStatsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.DecimalFormat;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserStatsServiceImpl implements UserStatsService {
    private final UserStatsRepository userStatsRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserStats getUserStats(Long userId) {
        log.debug("Getting stats for user: {}", userId);
        return userStatsRepository.findByUserId(userId);
    }

    @Override
    @Transactional
    public UserStats updateUserStats(Long userId) {
        log.debug("Updating stats for user: {}", userId);
        log.error("Updating stats for code not ready");
        
        /*User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        UserStats stats = userStatsRepository.findByUserId(userId);
        if (stats == null) {
            stats = new UserStats();
            stats.setUserId(userId);
        }

        // Update storage usage
        Long storageUsed = userStatsRepository.calculateStorageUsedByUser(userId);
        stats.setStorageUsed(storageUsed != null ? storageUsed : 0L);
        
        // Update language count
        Integer languageCount = userStatsRepository.countLanguagesByUser(userId);
        stats.setLanguageCount(languageCount != null ? languageCount : 0);

        // Update word count
        Long wordCount = userStatsRepository(userId);
        stats.setTotalWordCount(wordCount != null ? wordCount : 0L);

        // Update storage limit based on user subscription
        Long storageLimit = getStorageLimitBySubscription(user.getSubscriptionType());
        stats.setStorageLimit(storageLimit);

        // Calculate usage percentage
        double usagePercent = (storageUsed != null && storageLimit != null) 
            ? (storageUsed.doubleValue() / storageLimit.doubleValue()) * 100 
            : 0.0;
        stats.setStorageUsagePercent(Math.min(100.0, usagePercent));

        // Format storage values
        stats.setStorageUsedFormatted(formatFileSize(stats.getStorageUsed()));
        stats.setStorageLimitFormatted(formatFileSize(stats.getStorageLimit()));

        log.info("Stats updated for user {}: storage={}, languages={}, words={}", 
            userId, stats.getStorageUsedFormatted(), stats.getLanguageCount(), stats.getTotalWordCount());

        return userStatsRepository.save(stats);*/
        return null;
    }

    @Override
    @Transactional
    public void deleteUserStats(Long userId) {
        log.debug("Deleting stats for user: {}", userId);
        UserStats stats = userStatsRepository.findByUserId(userId);
        if (stats != null) {
            userStatsRepository.delete(stats);
            log.info("Stats deleted for user: {}", userId);
        }
    }

    private String formatFileSize(Long bytes) {
        if (bytes == null) return "0 B";
        
        final String[] units = new String[] { "B", "KB", "MB", "GB", "TB" };
        int digitGroups = (int) (Math.log10(bytes) / Math.log10(1024));
        return new DecimalFormat("#,##0.#")
                .format(bytes / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

    private Long getStorageLimitBySubscription(String subscriptionType) {
        return switch (subscriptionType) {
            case "BASIC" -> 10L * 1024 * 1024 * 1024;     // 10GB
            case "PRO" -> 50L * 1024 * 1024 * 1024;       // 50GB
            case "ENTERPRISE" -> 1024L * 1024 * 1024 * 1024; // 1TB
            default -> 5L * 1024 * 1024 * 1024;           // 5GB for free users
        };
    }
}
package com.filemanager.service;

import com.filemanager.model.UserStats;

public interface UserStatsService {
    UserStats getUserStats(Long userId);
    UserStats updateUserStats(Long userId);
    void deleteUserStats(Long userId);
}
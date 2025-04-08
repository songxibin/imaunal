package com.filemanager.repository;

import com.filemanager.model.UserStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserStatsRepository extends JpaRepository<UserStats, Long> {
    UserStats findByUserId(Long userId);

    @Query("SELECT SUM(d.fileSize) FROM Document d WHERE d.creator.id = :userId")
    Long calculateStorageUsedByUser(@Param("userId") Long userId);

    @Query("SELECT COUNT(DISTINCT d.language) FROM Document d WHERE d.creator.id = :userId")
    Integer countLanguagesByUser(@Param("userId") Long userId);


}
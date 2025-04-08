package com.filemanager.repository;

import com.filemanager.model.Document;
import com.filemanager.model.DocumentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long>, JpaSpecificationExecutor<Document> {
    Page<Document> findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
            String title, String description, Pageable pageable);

    @Query("SELECT d FROM Document d WHERE " +
           "LOWER(d.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(d.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(d.fileName) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Document> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    long countByCreatedAtAfter(LocalDateTime date);
    
    @Query("SELECT SUM(d.fileSize) FROM Document d WHERE d.creator.id = :userId")
    Long calculateStorageUsedByUser(@Param("userId") Long userId);

    @Query("SELECT COUNT(DISTINCT d.language) FROM Document d WHERE d.creator.id = :userId")
    Integer countLanguagesByUser(@Param("userId") Long userId);

//    @Query("SELECT SUM(d.wordCount) FROM Document d WHERE d.creator.id = :userId")
//    Long calculateTotalWordCountByUser(@Param("userId") Long userId);

    @Query("SELECT SUM(d.fileSize) FROM Document d")
    long sumFileSize();
    
    long countByStatus(DocumentStatus status);
    
    @Query("SELECT d FROM Document d WHERE d.status = 'PUBLISHED'")
    Page<Document> findAllPublished(Pageable pageable);
}
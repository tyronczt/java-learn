package com.secure.idquery.audit.repository;

import com.secure.idquery.audit.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    List<AuditLog> findByTraceId(String traceId);
    
    List<AuditLog> findByUserId(String userId);
    
    List<AuditLog> findByIdNumberHash(String idNumberHash);
    
    @Query("SELECT a FROM AuditLog a WHERE a.fuzzyIndex LIKE %:fuzzyPattern%")
    List<AuditLog> findByFuzzyIndexPattern(String fuzzyPattern);
    
    List<AuditLog> findByTimestampBetween(LocalDateTime start, LocalDateTime end);
}
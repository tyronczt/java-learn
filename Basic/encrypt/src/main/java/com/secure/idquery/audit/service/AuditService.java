package com.secure.idquery.audit.service;

import com.secure.idquery.audit.entity.AuditLog;
import com.secure.idquery.audit.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuditService {
    
    private final AuditLogRepository auditLogRepository;
    
    public String generateTraceId() {
        return UUID.randomUUID().toString();
    }
    
    public void logRequest(String traceId, String userId, String operation) {
        AuditLog auditLog = new AuditLog();
        auditLog.setTraceId(traceId);
        auditLog.setUserId(userId);
        auditLog.setOperation(operation);
        auditLog.setTimestamp(LocalDateTime.now());
        auditLog.setStatus("INITIATED");
        
        auditLogRepository.save(auditLog);
    }
    
    public void updateAuditLog(String traceId, String idNumberHash, String fuzzyIndex) {
        List<AuditLog> logs = auditLogRepository.findByTraceId(traceId);
        if (!logs.isEmpty()) {
            AuditLog log = logs.get(0);
            log.setIdNumberHash(idNumberHash);
            log.setFuzzyIndex(fuzzyIndex);
            log.setStatus("PROCESSING");
            auditLogRepository.save(log);
        }
    }
    
    public void completeAuditLog(String traceId, String status, String message) {
        List<AuditLog> logs = auditLogRepository.findByTraceId(traceId);
        if (!logs.isEmpty()) {
            AuditLog log = logs.get(0);
            log.setStatus(status);
            log.setMessage(message);
            auditLogRepository.save(log);
        }
    }
    
    public List<AuditLog> searchByIdNumberHash(String idNumberHash) {
        return auditLogRepository.findByIdNumberHash(idNumberHash);
    }
    
    public List<AuditLog> searchByFuzzyPattern(String fuzzyPattern) {
        return auditLogRepository.findByFuzzyIndexPattern(fuzzyPattern);
    }
    
    public List<AuditLog> getAuditLogsByTraceId(String traceId) {
        return auditLogRepository.findByTraceId(traceId);
    }
    
    public List<AuditLog> getAuditLogsByUserId(String userId) {
        return auditLogRepository.findByUserId(userId);
    }
    
    public List<AuditLog> getAuditLogsByTimeRange(LocalDateTime start, LocalDateTime end) {
        return auditLogRepository.findByTimestampBetween(start, end);
    }
}
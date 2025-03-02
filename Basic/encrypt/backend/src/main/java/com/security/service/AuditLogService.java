package com.security.service;

import com.security.crypto.CryptoUtils;
import com.security.entity.AuditLog;
import com.security.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 审计日志服务
 * 负责记录和查询审计日志，支持身份证号的模糊查询
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;
    
    @Value("${security.audit.retention-days:90}")
    private int retentionDays;
    
    @Value("${security.encryption.idcard-index.ngram-length:3}")
    private int ngramLength;
    
    /**
     * 记录查询请求
     * @param userId 用户ID
     * @param operation 操作类型
     * @param ipAddress IP地址
     * @return 追踪ID
     */
    public String logRequest(String userId, String operation, String ipAddress) {
        String traceId = generateTraceId();
        
        AuditLog auditLog = new AuditLog();
        auditLog.setTraceId(traceId);
        auditLog.setUserId(userId);
        auditLog.setOperation(operation);
        auditLog.setRequestTime(LocalDateTime.now());
        auditLog.setIpAddress(ipAddress);
        auditLog.setStatus("PENDING");
        
        auditLogRepository.save(auditLog);
        log.debug("记录查询请求: {}, 用户: {}", traceId, userId);
        
        return traceId;
    }
    
    /**
     * 更新审计日志的身份证信息
     * @param traceId 追踪ID
     * @param idCardNumber 身份证号（明文）
     */
    public void updateIdCardInfo(String traceId, String idCardNumber) {
        AuditLog auditLog = auditLogRepository.findByTraceId(traceId);
        if (auditLog == null) {
            log.warn("找不到追踪ID为{}的审计日志", traceId);
            return;
        }
        
        // 计算身份证哈希值（用于精确匹配）
        String idCardHash = CryptoUtils.sha256Hash(idCardNumber);
        auditLog.setIdCardHash(idCardHash);
        
        // 生成身份证模糊索引（用于模糊查询）
        String fuzzyIndex = generateFuzzyIndex(idCardNumber);
        auditLog.setIdCardFuzzyIndex(fuzzyIndex);
        
        auditLogRepository.save(auditLog);
        log.debug("更新审计日志身份证信息: {}", traceId);
    }
    
    /**
     * 更新审计日志状态
     * @param traceId 追踪ID
     * @param status 状态
     * @param extraInfo 额外信息
     */
    public void updateStatus(String traceId, String status, String extraInfo) {
        AuditLog auditLog = auditLogRepository.findByTraceId(traceId);
        if (auditLog == null) {
            log.warn("找不到追踪ID为{}的审计日志", traceId);
            return;
        }
        
        auditLog.setStatus(status);
        auditLog.setProcessTime(LocalDateTime.now());
        auditLog.setExtraInfo(extraInfo);
        
        auditLogRepository.save(auditLog);
        log.debug("更新审计日志状态: {}, 状态: {}", traceId, status);
    }
    
    /**
     * 根据身份证号模糊查询审计日志
     * @param idCardPartial 身份证号部分信息
     * @return 审计日志列表
     */
    public List<AuditLog> findByIdCardFuzzy(String idCardPartial) {
        log.debug("按身份证号模糊查询审计日志（无分页）");
        // 生成查询的N-gram哈希值
        Set<String> hashValues = generateNgramHashes(idCardPartial);
        
        // 使用模糊索引查询
        Set<AuditLog> results = new HashSet<>();
        for (String hashValue : hashValues) {
            String pattern = "%" + hashValue + "%";
            List<AuditLog> logs = auditLogRepository.findByIdCardFuzzyIndex(pattern);
            results.addAll(logs);
        }
        
        // 按请求时间排序
        return results.stream()
                .sorted(Comparator.comparing(AuditLog::getRequestTime).reversed())
                .collect(Collectors.toList());
    }
    
    /**
     * 根据身份证号精确查询审计日志
     * @param idCardNumber 身份证号
     * @return 审计日志列表
     */
    public List<AuditLog> findByIdCardExact(String idCardNumber) {
        log.debug("按身份证号精确查询审计日志（无分页）");
        String idCardHash = CryptoUtils.sha256Hash(idCardNumber);
        return auditLogRepository.findByIdCardHashOrderByRequestTimeDesc(idCardHash);
    }
    
    /**
     * 生成身份证号的模糊索引
     * 使用N-gram算法生成多个子串，并计算每个子串的哈希值
     * @param idCardNumber 身份证号
     * @return 模糊索引（多个哈希值，以逗号分隔）
     */
    private String generateFuzzyIndex(String idCardNumber) {
        Set<String> hashes = generateNgramHashes(idCardNumber);
        return String.join(",", hashes);
    }
    
    /**
     * 生成字符串的N-gram哈希值集合
     * @param text 输入字符串
     * @return 哈希值集合
     */
    private Set<String> generateNgramHashes(String text) {
        Set<String> hashes = new HashSet<>();
        if (text == null || text.length() < ngramLength) {
            return hashes;
        }
        
        // 生成N-gram子串
        for (int i = 0; i <= text.length() - ngramLength; i++) {
            String ngram = text.substring(i, i + ngramLength);
            String hash = CryptoUtils.sha256Hash(ngram);
            hashes.add(hash);
        }
        
        return hashes;
    }
    
    /**
     * 生成追踪ID
     * @return 追踪ID
     */
    private String generateTraceId() {
        return UUID.randomUUID().toString();
    }
    
    /**
     * 定时清理过期的审计日志
     * 每周日凌晨3点执行
     */
    @Scheduled(cron = "0 0 3 ? * SUN")
    public void cleanupExpiredLogs() {
        log.info("开始清理过期审计日志");
        LocalDateTime expiryDate = LocalDateTime.now().minusDays(retentionDays);
        
        long deletedCount = auditLogRepository.deleteByRequestTimeBefore(expiryDate);
        log.info("已清理{}条过期审计日志", deletedCount);
    }
    
    /**
     * 根据用户ID查询审计日志
     * @param userId 用户ID
     * @param pageable 分页对象
     * @return 审计日志列表
     */
    public List<AuditLog> findByUserId(String userId, Pageable pageable) {
        log.debug("按用户ID查询审计日志: {}", userId);
        List<AuditLog> allLogs = auditLogRepository.findByUserIdOrderByRequestTimeDesc(userId);
        
        // 手动实现分页
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), allLogs.size());
        
        return start <= end ? allLogs.subList(start, end) : Collections.emptyList();
    }
    
    /**
     * 统计用户的审计日志数量
     * @param userId 用户ID
     * @return 日志数量
     */
    public long countByUserId(String userId) {
        log.debug("统计用户的审计日志数量: {}", userId);
        List<AuditLog> logs = auditLogRepository.findByUserIdOrderByRequestTimeDesc(userId);
        return logs.size();
    }
    
    /**
     * 根据操作类型查询审计日志
     * @param operation 操作类型
     * @param pageable 分页对象
     * @return 审计日志列表
     */
    public List<AuditLog> findByOperation(String operation, Pageable pageable) {
        log.debug("按操作类型查询审计日志: {}", operation);
        List<AuditLog> allLogs = auditLogRepository.findByOperationOrderByRequestTimeDesc(operation);
        
        // 手动实现分页
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), allLogs.size());
        
        return start <= end ? allLogs.subList(start, end) : Collections.emptyList();
    }
    
    /**
     * 统计特定操作类型的审计日志数量
     * @param operation 操作类型
     * @return 日志数量
     */
    public long countByOperation(String operation) {
        log.debug("统计特定操作类型的审计日志数量: {}", operation);
        List<AuditLog> logs = auditLogRepository.findByOperationOrderByRequestTimeDesc(operation);
        return logs.size();
    }
    
    /**
     * 根据时间范围查询审计日志
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param pageable 分页对象
     * @return 审计日志列表
     */
    public List<AuditLog> findByTimeRange(LocalDateTime startTime, LocalDateTime endTime, Pageable pageable) {
        log.debug("按时间范围查询审计日志: {} 至 {}", startTime, endTime);
        List<AuditLog> allLogs = auditLogRepository.findByRequestTimeBetweenOrderByRequestTimeDesc(startTime, endTime);
        
        // 手动实现分页
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), allLogs.size());
        
        return start <= end ? allLogs.subList(start, end) : Collections.emptyList();
    }
    
    /**
     * 统计时间范围内的审计日志数量
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 日志数量
     */
    public long countByTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        log.debug("统计时间范围内的审计日志数量: {} 至 {}", startTime, endTime);
        List<AuditLog> logs = auditLogRepository.findByRequestTimeBetweenOrderByRequestTimeDesc(startTime, endTime);
        return logs.size();
    }
    
    /**
     * 根据身份证号模糊查询审计日志（带分页）
     * @param idCardPartial 身份证号部分信息
     * @param pageable 分页参数
     * @return 审计日志列表
     */
    public List<AuditLog> findByIdCardFuzzy(String idCardPartial, Pageable pageable) {
        log.debug("按身份证号模糊查询审计日志");
        // 生成查询的N-gram哈希值
        Set<String> hashValues = generateNgramHashes(idCardPartial);
        
        // 使用模糊索引查询
        Set<AuditLog> results = new HashSet<>();
        for (String hashValue : hashValues) {
            String pattern = "%" + hashValue + "%";
            List<AuditLog> logs = auditLogRepository.findByIdCardFuzzyIndex(pattern);
            results.addAll(logs);
        }
        
        // 按请求时间排序
        List<AuditLog> sortedResults = results.stream()
                .sorted(Comparator.comparing(AuditLog::getRequestTime).reversed())
                .collect(Collectors.toList());
        
        // 手动实现分页
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), sortedResults.size());
        
        return start <= end ? sortedResults.subList(start, end) : Collections.emptyList();
    }
    
    /**
     * 根据身份证号精确查询审计日志（带分页）
     * @param idCardNumber 身份证号
     * @param pageable 分页参数
     * @return 审计日志列表
     */
    public List<AuditLog> findByIdCardExact(String idCardNumber, Pageable pageable) {
        log.debug("按身份证号精确查询审计日志");
        String idCardHash = CryptoUtils.sha256Hash(idCardNumber);
        List<AuditLog> allLogs = auditLogRepository.findByIdCardHashOrderByRequestTimeDesc(idCardHash);
        
        // 手动实现分页
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), allLogs.size());
        
        return start <= end ? allLogs.subList(start, end) : Collections.emptyList();
    }
    
    /**
     * 统计符合身份证号模糊查询条件的日志数量
     * @param idCardPartial 身份证号部分信息
     * @return 日志数量
     */
    public long countByIdCardFuzzy(String idCardPartial) {
        log.debug("统计符合身份证号模糊查询条件的日志数量");
        // 生成查询的N-gram哈希值
        Set<String> hashValues = generateNgramHashes(idCardPartial);
        
        // 使用模糊索引查询
        Set<AuditLog> results = new HashSet<>();
        for (String hashValue : hashValues) {
            String pattern = "%" + hashValue + "%";
            List<AuditLog> logs = auditLogRepository.findByIdCardFuzzyIndex(pattern);
            results.addAll(logs);
        }
        
        return results.size();
    }
    
    /**
     * 统计符合身份证号精确查询条件的日志数量
     * @param idCardNumber 身份证号
     * @return 日志数量
     */
    public long countByIdCardExact(String idCardNumber) {
        log.debug("统计符合身份证号精确查询条件的日志数量");
        String idCardHash = CryptoUtils.sha256Hash(idCardNumber);
        List<AuditLog> logs = auditLogRepository.findByIdCardHashOrderByRequestTimeDesc(idCardHash);
        return logs.size();
    }
    
    /**
     * 查询最近的审计日志
     * @param pageable 分页参数
     * @return 审计日志列表
     */
    public List<AuditLog> findRecent(Pageable pageable) {
        log.debug("查询最近的审计日志");
        List<AuditLog> allLogs = auditLogRepository.findAll()
                .stream()
                .sorted(Comparator.comparing(AuditLog::getRequestTime).reversed())
                .collect(Collectors.toList());
        
        // 手动实现分页
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), allLogs.size());
        
        return start <= end ? allLogs.subList(start, end) : Collections.emptyList();
    }
    
    /**
     * 统计所有审计日志数量
     * @return 日志数量
     */
    public long count() {
        log.debug("统计所有审计日志数量");
        return auditLogRepository.count();
    }
}
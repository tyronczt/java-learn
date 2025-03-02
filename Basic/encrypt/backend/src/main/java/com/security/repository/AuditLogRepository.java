package com.security.repository;

import com.security.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 审计日志存储库
 * 提供对审计日志的数据库操作，支持身份证号的精确和模糊查询
 */
@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    /**
     * 根据用户ID查询审计日志
     * @param userId 用户ID
     * @return 审计日志列表
     */
    List<AuditLog> findByUserIdOrderByRequestTimeDesc(String userId);

    /**
     * 根据身份证号哈希值精确查询
     * @param idCardHash 身份证号哈希值
     * @return 审计日志列表
     */
    List<AuditLog> findByIdCardHashOrderByRequestTimeDesc(String idCardHash);

    /**
     * 根据身份证号模糊索引进行模糊查询
     * 使用LIKE操作符查询包含指定模糊索引的记录
     * @param fuzzyIndexPattern 模糊索引模式，格式为"%hashValue%"
     * @return 审计日志列表
     */
    @Query("SELECT a FROM AuditLog a WHERE a.idCardFuzzyIndex LIKE :fuzzyIndexPattern ORDER BY a.requestTime DESC")
    List<AuditLog> findByIdCardFuzzyIndex(@Param("fuzzyIndexPattern") String fuzzyIndexPattern);

    /**
     * 根据时间范围查询审计日志
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 审计日志列表
     */
    List<AuditLog> findByRequestTimeBetweenOrderByRequestTimeDesc(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 根据操作类型查询审计日志
     * @param operation 操作类型
     * @return 审计日志列表
     */
    List<AuditLog> findByOperationOrderByRequestTimeDesc(String operation);

    /**
     * 根据追踪ID查询审计日志
     * @param traceId 追踪ID
     * @return 审计日志
     */
    AuditLog findByTraceId(String traceId);

    /**
     * 删除指定日期之前的审计日志
     * @param date 日期
     * @return 删除的记录数
     */
    long deleteByRequestTimeBefore(LocalDateTime date);
}
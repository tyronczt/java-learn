package com.security.entity;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 审计日志实体
 * 记录用户查询操作，支持身份证号的模糊查询
 */
@Entity
@Table(name = "audit_logs")
@Data
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 请求追踪ID
     */
    @Column(name = "trace_id", nullable = false)
    private String traceId;

    /**
     * 用户ID
     */
    @Column(name = "user_id", nullable = false)
    private String userId;

    /**
     * 操作类型
     */
    @Column(name = "operation", nullable = false)
    private String operation;

    /**
     * 身份证号哈希值（用于精确匹配）
     */
    @Column(name = "id_card_hash")
    private String idCardHash;

    /**
     * 身份证号模糊索引（用于模糊查询）
     * 存储多个N-gram的哈希值，以逗号分隔
     */
    @Column(name = "id_card_fuzzy_index", length = 4096)
    private String idCardFuzzyIndex;

    /**
     * 请求时间
     */
    @Column(name = "request_time", nullable = false)
    private LocalDateTime requestTime;

    /**
     * 处理时间
     */
    @Column(name = "process_time")
    private LocalDateTime processTime;

    /**
     * 请求状态
     */
    @Column(name = "status")
    private String status;

    /**
     * 请求IP地址
     */
    @Column(name = "ip_address")
    private String ipAddress;

    /**
     * 额外信息（JSON格式）
     */
    @Column(name = "extra_info", length = 2048)
    private String extraInfo;
}
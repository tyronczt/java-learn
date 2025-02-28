package com.secure.idquery.audit.entity;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs")
@Data
public class AuditLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String traceId;
    private String userId;
    private String operation;
    private LocalDateTime timestamp;
    
    private String idNumberHash; // 身份证哈希（用于精确查询）
    private String fuzzyIndex; // 模糊索引（用于模糊查询）
    
    private String status;
    private String message;
    
    @Lob
    private String requestDetails; // 加密的请求详情
    
    @Lob
    private String responseDetails; // 加密的响应详情
}
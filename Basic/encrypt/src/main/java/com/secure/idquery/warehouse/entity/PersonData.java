package com.secure.idquery.warehouse.entity;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "person_data")
@Data
public class PersonData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String idNumberHash; // 身份证哈希（用于精确查询）
    private String fuzzyIndex; // 模糊索引（用于模糊查询）
    
    @Lob
    private String encryptedData; // 加密的个人数据（JSON格式）
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
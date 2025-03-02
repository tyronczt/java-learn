package com.security.entity;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 用户密钥对实体
 * 存储用户的RSA公钥和私钥
 */
@Entity
@Table(name = "user_key_pairs")
@Data
public class UserKeyPair {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 用户ID
     */
    @Column(name = "user_id", nullable = false, unique = true)
    private String userId;

    /**
     * RSA公钥（Base64编码）
     */
    @Column(name = "public_key", nullable = false, length = 2048)
    private String publicKey;

    /**
     * RSA私钥（Base64编码）
     */
    @Column(name = "private_key", nullable = false, length = 4096)
    private String privateKey;

    /**
     * 创建时间
     */
    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdDate;

    /**
     * 过期时间
     */
    @Column(name = "expiry_date", nullable = false)
    private LocalDateTime expiryDate;
}
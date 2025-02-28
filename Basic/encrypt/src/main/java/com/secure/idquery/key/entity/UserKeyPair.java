package com.secure.idquery.key.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_key_pairs")
@Data
public class UserKeyPair {
    @Id
    private String userId;
    
    @Lob
    private String publicKey; // Base64编码的RSA公钥
    
    @Lob
    private String privateKey; // Base64编码的RSA私钥
    
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
    private boolean active;
}
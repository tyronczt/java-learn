package com.secure.idquery.common.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SessionKey {
    private String keyValue; // Base64编码的AES密钥
    private String iv; // Base64编码的初始化向量
    private String userId; // 用户ID
    private long timestamp; // 创建时间戳
    private long expiryTime; // 过期时间戳
}
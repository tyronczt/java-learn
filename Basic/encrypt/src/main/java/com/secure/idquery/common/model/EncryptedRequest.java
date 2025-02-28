package com.secure.idquery.common.model;

import lombok.Data;

@Data
public class EncryptedRequest {
    private String encryptedSessionKey; // RSA加密的AES会话密钥
    private String iv; // Base64编码的初始化向量
    private String encryptedData; // AES-GCM加密的数据
    private String userId; // 用户ID
}
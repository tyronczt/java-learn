package com.secure.idquery.common.model;

import lombok.Data;

@Data
public class EncryptedResponse {
    private String encryptedData; // AES-GCM加密的数据
    private String traceId; // 请求追踪ID
    private boolean success; // 请求是否成功
    private String message; // 消息（如错误信息）
}
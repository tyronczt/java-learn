package com.secure.idquery.common.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInfo {
    private String id;
    private String name;
    private String encryptedIdNumber; // 加密的身份证号
    private String idNumberHash; // 身份证号哈希值（用于索引）
    private String fuzzyIndex; // 模糊索引（用于模糊查询）
    private String address;
    private String phone;
    private String email;
    private String encryptedData; // 其他加密数据
}
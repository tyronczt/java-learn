package com.security.service;

import com.security.crypto.CryptoUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 加密服务
 * 负责处理数据加密和解密，与会话密钥交互，以及提供模糊索引生成功能
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class CryptoService {

    private final KeyService keyService;
    private final SessionService sessionService;
    
    // 用户会话ID缓存，key为用户ID，value为会话ID
    private final Map<String, String> userSessionIdCache = new ConcurrentHashMap<>();
    
    /**
     * 使用用户私钥解密数据
     * 
     * @param userId 用户ID
     * @param encryptedData 加密数据
     * @return 解密后的数据
     */
    public String decryptWithPrivateKey(String userId, String encryptedData) {
        log.debug("使用用户[{}]私钥解密数据", userId);
        return keyService.decryptWithUserPrivateKey(userId, encryptedData);
    }
    
    /**
     * 使用会话密钥解密数据
     * 
     * @param sessionId 会话ID
     * @param encryptedData 加密数据
     * @return 解密后的数据
     */
    public String decryptWithSessionKey(String sessionId, String encryptedData) {
        log.debug("使用会话密钥解密数据, 会话: {}", sessionId);
        SessionService.SessionInfo sessionInfo = sessionService.getSession(sessionId);
        if (sessionInfo == null) {
            throw new IllegalStateException("会话不存在");
        }
        
        return sessionService.decryptWithSessionKey(sessionId, encryptedData);
    }
    
    /**
     * 使用会话密钥加密数据
     * 
     * @param sessionId 会话ID
     * @param data 明文数据
     * @return 加密后的数据
     */
    public String encryptWithSessionKey(String sessionId, String data) {
        log.debug("使用会话密钥加密数据, 会话: {}", sessionId);
        SessionService.SessionInfo sessionInfo = sessionService.getSession(sessionId);
        if (sessionInfo == null) {
            throw new IllegalStateException("会话不存在");
        }
        
        return sessionService.encryptWithSessionKey(sessionId, data);
    }
    
    /**
     * 解密传入的身份证号码
     * 根据实际情况，可能使用会话密钥或直接处理
     * 
     * @param userId 用户ID
     * @param encryptedIdCard 加密的身份证号
     * @return 解密后的身份证号
     */
    public String decryptData(String userId, String encryptedIdCard) {
        log.info("解密用户[{}]的身份证数据", userId);
        // 获取当前用户的会话ID
        String sessionId = getCurrentSessionId(userId);
        return decryptWithSessionKey(sessionId, encryptedIdCard);
    }
    
    /**
     * 加密数据对象
     * 
     * @param userId 用户ID
     * @param data 待加密的数据
     * @return 加密后的数据
     */
    public String encryptData(String userId, String data) {
        // 获取当前用户的会话ID
        String sessionId = getCurrentSessionId(userId);
        return encryptWithSessionKey(sessionId, data);
    }
    
    /**
     * 获取用户公钥
     * 
     * @param userId 用户ID
     * @return RSA公钥字符串
     */
    public String getUserPublicKey(String userId) {
        return keyService.getUserPublicKey(userId);
    }
    
    /**
     * 获取用户当前会话ID
     * 简化版的实现，实际项目中可能从上下文中获取
     * 
     * @param userId 用户ID
     * @return 会话ID
     */
    private String getCurrentSessionId(String userId) {
        // 实际项目中应当从请求上下文、Token等获取
        // 这里简化处理，直接从缓存获取
        return userSessionIdCache.getOrDefault(userId, "default-session");
    }
    
    /**
     * 关联用户和会话ID
     * 
     * @param userId 用户ID
     * @param sessionId 会话ID
     */
    public void associateUserWithSession(String userId, String sessionId) {
        userSessionIdCache.put(userId, sessionId);
    }
    
    /**
     * 计算字符串的SHA-256哈希值
     * 
     * @param input 输入字符串
     * @return Base64编码的哈希值
     */
    public String sha256Hash(String input) {
        return CryptoUtils.sha256Hash(input);
    }
    
    /**
     * 生成N-gram模糊索引
     * 将输入字符串分解为重叠的n个字符片段，并计算每个片段的哈希值
     * 
     * @param input 输入字符串
     * @param n N-gram大小
     * @return N-gram索引数组
     */
    public List<String> generateNGramIndex(String input, int n) {
        if (input == null || input.length() < n) {
            return new ArrayList<>();
        }
        
        List<String> ngrams = new ArrayList<>();
        for (int i = 0; i <= input.length() - n; i++) {
            String ngram = input.substring(i, i + n);
            // 对每个n-gram计算哈希值作为索引
            ngrams.add(CryptoUtils.sha256Hash(ngram));
        }
        
        log.debug("为输入生成了{}个N-gram索引", ngrams.size());
        return ngrams;
    }
    
    /**
     * 生成随机AES密钥
     * 
     * @param keySize 密钥大小（位）
     * @return AES密钥（Base64编码）
     */
    public String generateAESKey(int keySize) {
        return CryptoUtils.generateAESKey(keySize);
    }
    
    /**
     * 生成随机初始化向量
     * 
     * @param length 向量长度
     * @return 随机初始化向量
     */
    public String generateRandomIV(int length) {
        return CryptoUtils.generateRandomString(length);
    }
    
    /**
     * 检查两个模糊索引的匹配度
     * 用于模糊查询时判断两个索引的相似程度
     * 
     * @param sourceIndexes 源索引列表
     * @param targetIndexes 目标索引列表
     * @param threshold 匹配阈值（0-1之间）
     * @return 是否匹配
     */
    public boolean matchFuzzyIndexes(List<String> sourceIndexes, List<String> targetIndexes, double threshold) {
        if (sourceIndexes == null || targetIndexes == null || sourceIndexes.isEmpty() || targetIndexes.isEmpty()) {
            return false;
        }
        
        int matches = 0;
        for (String sourceIndex : sourceIndexes) {
            if (targetIndexes.contains(sourceIndex)) {
                matches++;
            }
        }
        
        // 计算匹配度
        double matchRatio = (double) matches / Math.min(sourceIndexes.size(), targetIndexes.size());
        log.debug("模糊索引匹配度: {}", matchRatio);
        
        return matchRatio >= threshold;
    }
}
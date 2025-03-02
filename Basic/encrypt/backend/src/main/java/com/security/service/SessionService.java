package com.security.service;

import com.security.crypto.CryptoUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 会话服务
 * 管理用户的加密会话，包括会话密钥和IV
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class SessionService {

    private final KeyService keyService;
    
    // 会话信息缓存，key为会话ID，value为会话信息
    private final Map<String, SessionInfo> sessionCache = new ConcurrentHashMap<>();
    
    /**
     * 初始化用户会话
     * @param userId 用户ID
     * @param encryptedSessionKey 加密的会话密钥
     * @param iv 初始化向量
     * @return 会话ID
     */
    public String initSession(String userId, String encryptedSessionKey, String iv) {
        // 使用用户私钥解密会话密钥
        String sessionKey = keyService.decryptWithUserPrivateKey(userId, encryptedSessionKey);
        
        // 生成会话ID
        String sessionId = CryptoUtils.generateRandomString(32);
        
        // 创建会话信息并缓存
        SessionInfo sessionInfo = new SessionInfo();
        sessionInfo.setUserId(userId);
        sessionInfo.setSessionKey(sessionKey);
        sessionInfo.setIv(iv);
        sessionInfo.setCreatedTime(LocalDateTime.now());
        sessionInfo.setLastAccessTime(LocalDateTime.now());
        
        sessionCache.put(sessionId, sessionInfo);
        
        log.info("用户{}初始化会话成功，会话ID: {}", userId, sessionId);
        return sessionId;
    }
    
    /**
     * 获取会话信息
     * @param sessionId 会话ID
     * @return 会话信息
     */
    public SessionInfo getSession(String sessionId) {
        SessionInfo sessionInfo = sessionCache.get(sessionId);
        if (sessionInfo != null) {
            // 更新最后访问时间
            sessionInfo.setLastAccessTime(LocalDateTime.now());
        }
        return sessionInfo;
    }
    
    /**
     * 使用会话密钥加密数据
     * @param sessionId 会话ID
     * @param data 要加密的数据
     * @return 加密后的数据
     */
    public String encryptWithSessionKey(String sessionId, String data) {
        SessionInfo sessionInfo = getSession(sessionId);
        if (sessionInfo == null) {
            throw new RuntimeException("会话不存在或已过期");
        }
        
        return CryptoUtils.encryptWithAES(sessionInfo.getSessionKey(), sessionInfo.getIv(), data);
    }
    
    /**
     * 使用会话密钥解密数据
     * @param sessionId 会话ID
     * @param encryptedData 加密的数据
     * @return 解密后的数据
     */
    public String decryptWithSessionKey(String sessionId, String encryptedData) {
        SessionInfo sessionInfo = getSession(sessionId);
        if (sessionInfo == null) {
            throw new RuntimeException("会话不存在或已过期");
        }
        
        return CryptoUtils.decryptWithAES(sessionInfo.getSessionKey(), sessionInfo.getIv(), encryptedData);
    }
    
    /**
     * 清理过期的会话
     * 可以通过定时任务调用此方法
     * @param expiryMinutes 过期时间（分钟）
     */
    public void cleanupExpiredSessions(int expiryMinutes) {
        LocalDateTime expiryTime = LocalDateTime.now().minusMinutes(expiryMinutes);
        int count = 0;
        
        for (Map.Entry<String, SessionInfo> entry : sessionCache.entrySet()) {
            if (entry.getValue().getLastAccessTime().isBefore(expiryTime)) {
                sessionCache.remove(entry.getKey());
                count++;
            }
        }
        
        if (count > 0) {
            log.info("清理了{}个过期会话", count);
        }
    }
    
    /**
     * 会话信息类
     */
    public static class SessionInfo {
        private String userId;
        private String sessionKey;
        private String iv;
        private LocalDateTime createdTime;
        private LocalDateTime lastAccessTime;
        
        // Getters and Setters
        public String getUserId() {
            return userId;
        }
        
        public void setUserId(String userId) {
            this.userId = userId;
        }
        
        public String getSessionKey() {
            return sessionKey;
        }
        
        public void setSessionKey(String sessionKey) {
            this.sessionKey = sessionKey;
        }
        
        public String getIv() {
            return iv;
        }
        
        public void setIv(String iv) {
            this.iv = iv;
        }
        
        public LocalDateTime getCreatedTime() {
            return createdTime;
        }
        
        public void setCreatedTime(LocalDateTime createdTime) {
            this.createdTime = createdTime;
        }
        
        public LocalDateTime getLastAccessTime() {
            return lastAccessTime;
        }
        
        public void setLastAccessTime(LocalDateTime lastAccessTime) {
            this.lastAccessTime = lastAccessTime;
        }
    }
}
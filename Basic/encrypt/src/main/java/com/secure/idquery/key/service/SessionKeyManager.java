package com.secure.idquery.key.service;

import com.secure.idquery.common.model.SessionKey;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class SessionKeyManager {
    private final Map<String, SessionKey> sessionKeyMap = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    
    // 会话密钥有效期（毫秒）
    private static final long SESSION_KEY_TTL = 30 * 60 * 1000; // 30分钟
    
    public SessionKeyManager() {
        // 定期清理过期的会话密钥
        scheduler.scheduleAtFixedRate(this::cleanupExpiredKeys, 5, 5, TimeUnit.MINUTES);
    }
    
    public void storeSessionKey(String userId, String keyValue, String iv) {
        long now = System.currentTimeMillis();
        SessionKey sessionKey = new SessionKey(
                keyValue,
                iv,
                userId,
                now,
                now + SESSION_KEY_TTL
        );
        sessionKeyMap.put(userId, sessionKey);
    }
    
    public SessionKey getSessionKey(String userId) {
        SessionKey key = sessionKeyMap.get(userId);
        if (key != null && System.currentTimeMillis() > key.getExpiryTime()) {
            sessionKeyMap.remove(userId);
            return null;
        }
        return key;
    }
    
    private void cleanupExpiredKeys() {
        long now = System.currentTimeMillis();
        sessionKeyMap.entrySet().removeIf(entry -> entry.getValue().getExpiryTime() < now);
    }
}
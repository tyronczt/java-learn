package com.secure.idquery.key.service;

import com.secure.idquery.common.model.SessionKey;
import com.secure.idquery.common.util.CryptoUtils;
import com.secure.idquery.key.entity.UserKeyPair;
import com.secure.idquery.key.repository.UserKeyPairRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class KeyService {
    
    private final UserKeyPairRepository keyPairRepository;
    private final SessionKeyManager sessionKeyManager;
    
    @PostConstruct
    public void init() {
        // 初始化时确保系统中有默认用户的密钥对
        ensureUserKeyPair("user1");
        ensureUserKeyPair("user2");
    }
    
    public String getUserPublicKey(String userId) {
        UserKeyPair keyPair = getUserKeyPairOrCreate(userId);
        return keyPair.getPublicKey();
    }
    
    public byte[] decryptWithUserPrivateKey(String userId, byte[] encryptedData) {
        try {
            UserKeyPair keyPair = getUserKeyPairOrCreate(userId);
            PrivateKey privateKey = CryptoUtils.base64ToRSAPrivateKey(keyPair.getPrivateKey());
            return CryptoUtils.rsaDecrypt(encryptedData, privateKey);
        } catch (Exception e) {
            log.error("Failed to decrypt with user private key", e);
            throw new RuntimeException("解密失败", e);
        }
    }
    
    public void storeSessionKey(String userId, String encryptedSessionKey, String iv) {
        try {
            // 使用用户私钥解密会话密钥
            byte[] encryptedKeyBytes = java.util.Base64.getDecoder().decode(encryptedSessionKey);
            byte[] decryptedKeyBytes = decryptWithUserPrivateKey(userId, encryptedKeyBytes);
            String sessionKeyBase64 = new String(decryptedKeyBytes);
            
            // 存储会话密钥
            sessionKeyManager.storeSessionKey(userId, sessionKeyBase64, iv);
        } catch (Exception e) {
            log.error("Failed to store session key", e);
            throw new RuntimeException("存储会话密钥失败", e);
        }
    }
    
    public String getSessionKey(String userId) {
        return Optional.ofNullable(sessionKeyManager.getSessionKey(userId))
                .map(SessionKey::getKeyValue)
                .orElseThrow(() -> new RuntimeException("会话密钥不存在或已过期"));
    }
    
    public String getSessionIV(String userId) {
        return Optional.ofNullable(sessionKeyManager.getSessionKey(userId))
                .map(SessionKey::getIv)
                .orElseThrow(() -> new RuntimeException("会话IV不存在或已过期"));
    }
    
    private UserKeyPair getUserKeyPairOrCreate(String userId) {
        return keyPairRepository.findByUserIdAndActiveTrue(userId)
                .orElseGet(() -> createAndSaveUserKeyPair(userId));
    }
    
    private void ensureUserKeyPair(String userId) {
        if (!keyPairRepository.findByUserIdAndActiveTrue(userId).isPresent()) {
            createAndSaveUserKeyPair(userId);
        }
    }
    
    private UserKeyPair createAndSaveUserKeyPair(String userId) {
        try {
            KeyPair keyPair = CryptoUtils.generateRSAKeyPair();
            
            UserKeyPair userKeyPair = new UserKeyPair();
            userKeyPair.setUserId(userId);
            userKeyPair.setPublicKey(CryptoUtils.keyToBase64(keyPair.getPublic()));
            userKeyPair.setPrivateKey(CryptoUtils.keyToBase64(keyPair.getPrivate()));
            userKeyPair.setCreatedAt(LocalDateTime.now());
            userKeyPair.setExpiresAt(LocalDateTime.now().plusYears(1));
            userKeyPair.setActive(true);
            
            return keyPairRepository.save(userKeyPair);
        } catch (NoSuchAlgorithmException e) {
            log.error("Failed to generate RSA key pair", e);
            throw new RuntimeException("生成RSA密钥对失败", e);
        }
    }
}
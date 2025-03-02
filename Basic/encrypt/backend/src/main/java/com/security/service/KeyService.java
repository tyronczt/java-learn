package com.security.service;

import com.security.crypto.CryptoUtils;
import com.security.entity.UserKeyPair;
import com.security.repository.UserKeyPairRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 密钥服务
 * 负责生成、存储和管理用户RSA密钥对
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class KeyService {

    private final UserKeyPairRepository userKeyPairRepository;
    
    @Value("${security.rsa.key-size:2048}")
    private int keySize;
    
    @Value("${security.rsa.key-cache-days:30}")
    private int keyCacheDays;
    
    // 内存缓存，提高性能
    private final Map<String, UserKeyPair> keyPairCache = new ConcurrentHashMap<>();
    
    @PostConstruct
    public void init() {
        log.info("初始化密钥服务，RSA密钥大小: {}位", keySize);
        // 加载所有有效的密钥到缓存
        userKeyPairRepository.findAllByExpiryDateAfter(LocalDateTime.now())
                .forEach(keyPair -> keyPairCache.put(keyPair.getUserId(), keyPair));
        log.info("已加载{}个有效密钥到缓存", keyPairCache.size());
    }
    
    /**
     * 获取用户的公钥
     * @param userId 用户ID
     * @return Base64编码的公钥
     */
    public String getUserPublicKey(String userId) {
        UserKeyPair keyPair = getUserKeyPair(userId);
        return keyPair.getPublicKey();
    }
    
    /**
     * 使用用户私钥解密数据
     * @param userId 用户ID
     * @param encryptedData 加密的数据
     * @return 解密后的数据
     */
    public String decryptWithUserPrivateKey(String userId, String encryptedData) {
        UserKeyPair keyPair = getUserKeyPair(userId);
        PrivateKey privateKey = CryptoUtils.parsePrivateKey(keyPair.getPrivateKey());
        return CryptoUtils.decryptWithRSA(privateKey, encryptedData);
    }
    
    /**
     * 获取用户密钥对，如果不存在或已过期则生成新的
     * @param userId 用户ID
     * @return 用户密钥对
     */
    private UserKeyPair getUserKeyPair(String userId) {
        // 先从缓存获取
        UserKeyPair keyPair = keyPairCache.get(userId);
        
        // 如果缓存中没有或已过期，则从数据库获取
        if (keyPair == null || keyPair.getExpiryDate().isBefore(LocalDateTime.now())) {
            synchronized (this) {
                // 双重检查锁定
                keyPair = keyPairCache.get(userId);
                if (keyPair == null || keyPair.getExpiryDate().isBefore(LocalDateTime.now())) {
                    // 从数据库获取
                    Optional<UserKeyPair> optionalKeyPair = userKeyPairRepository.findByUserIdAndExpiryDateAfter(
                            userId, LocalDateTime.now());
                    
                    if (optionalKeyPair.isPresent()) {
                        keyPair = optionalKeyPair.get();
                    } else {
                        // 生成新的密钥对
                        keyPair = generateNewKeyPair(userId);
                    }
                    
                    // 更新缓存
                    keyPairCache.put(userId, keyPair);
                }
            }
        }
        
        return keyPair;
    }
    
    /**
     * 生成新的RSA密钥对
     * @param userId 用户ID
     * @return 保存的用户密钥对实体
     */
    private UserKeyPair generateNewKeyPair(String userId) {
        log.info("为用户{}生成新的RSA密钥对", userId);
        
        // 生成RSA密钥对
        KeyPair keyPair = CryptoUtils.generateRSAKeyPair(keySize);
        
        // 转换为Base64编码
        String publicKeyBase64 = Base64.encodeBase64String(keyPair.getPublic().getEncoded());
        String privateKeyBase64 = Base64.encodeBase64String(keyPair.getPrivate().getEncoded());
        
        // 创建实体并保存
        UserKeyPair userKeyPair = new UserKeyPair();
        userKeyPair.setUserId(userId);
        userKeyPair.setPublicKey(publicKeyBase64);
        userKeyPair.setPrivateKey(privateKeyBase64);
        userKeyPair.setCreatedDate(LocalDateTime.now());
        userKeyPair.setExpiryDate(LocalDateTime.now().plusDays(keyCacheDays));
        
        return userKeyPairRepository.save(userKeyPair);
    }
    
    /**
     * 定时清理过期的密钥
     * 每天凌晨2点执行
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void cleanupExpiredKeys() {
        log.info("开始清理过期密钥");
        LocalDateTime now = LocalDateTime.now();
        
        // 清理数据库中的过期密钥
        long deletedCount = userKeyPairRepository.deleteByExpiryDateBefore(now);
        
        // 清理缓存中的过期密钥
        keyPairCache.entrySet().removeIf(entry -> entry.getValue().getExpiryDate().isBefore(now));
        
        log.info("已清理{}个过期密钥", deletedCount);
    }
    
    /**
     * 使用公钥加密数据
     * @param publicKeyBase64 Base64编码的公钥
     * @param data 要加密的数据
     * @return 加密后的数据
     */
    public String encryptWithPublicKey(String publicKeyBase64, String data) {
        PublicKey publicKey = CryptoUtils.parsePublicKey(publicKeyBase64);
        return CryptoUtils.encryptWithRSA(publicKey, data);
    }
}
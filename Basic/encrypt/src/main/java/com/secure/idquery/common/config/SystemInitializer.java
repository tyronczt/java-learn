package com.secure.idquery.common.config;

import com.secure.idquery.common.util.CryptoUtils;
import com.secure.idquery.key.service.KeyService;
import com.secure.idquery.key.service.SessionKeyManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;

@Component
@RequiredArgsConstructor
@Slf4j
public class SystemInitializer implements CommandLineRunner {

    private final KeyService keyService;
    private final SessionKeyManager sessionKeyManager;

    @Override
    public void run(String... args) throws Exception {
        log.info("初始化系统密钥...");
        
        try {
            // 初始化系统用户密钥
            initializeUserSession("system");
            
            // 初始化测试用户密钥
            initializeUserSession("user1");
            initializeUserSession("user2");
            
            log.info("系统初始化完成");
        } catch (Exception e) {
            log.error("系统初始化失败", e);
            throw e;
        }
    }
    
    private void initializeUserSession(String userId) throws Exception {
        // 为用户创建密钥对
        String userPublicKey = keyService.getUserPublicKey(userId);
        log.info("{}公钥已生成: {}", userId, userPublicKey.substring(0, 20) + "...");
        
        // 生成会话密钥
        SecretKey aesKey = CryptoUtils.generateAESKey();
        String sessionKey = Base64.getEncoder().encodeToString(aesKey.getEncoded());
        
        // 生成IV - 确保使用16字节IV (CBC模式需要)
        byte[] iv = new byte[16];
        new java.security.SecureRandom().nextBytes(iv);
        String ivBase64 = Base64.getEncoder().encodeToString(iv);
        
        // 存储会话密钥
        sessionKeyManager.storeSessionKey(userId, sessionKey, ivBase64);
        log.info("{}会话密钥已生成并存储", userId);
    }
}
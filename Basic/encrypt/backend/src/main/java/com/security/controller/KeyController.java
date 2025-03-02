package com.security.controller;

import com.security.service.CryptoService;
import com.security.service.KeyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 密钥控制器
 * 提供获取用户RSA公钥的API接口
 */
@RestController
@RequestMapping("/keys")
@RequiredArgsConstructor
@Slf4j
public class KeyController {

    private final KeyService keyService;
    private final CryptoService cryptoService;

    /**
     * 获取用户的RSA公钥
     * @param userId 用户ID
     * @return 包含公钥的响应
     */
    @GetMapping("/public/{userId}")
    public ResponseEntity<Map<String, String>> getUserPublicKey(@PathVariable String userId) {
        log.debug("获取用户{}的公钥", userId);
        String publicKey = cryptoService.getUserPublicKey(userId);
        
        Map<String, String> response = new HashMap<>();
        response.put("publicKey", publicKey);
        
        return ResponseEntity.ok(response);
    }
}
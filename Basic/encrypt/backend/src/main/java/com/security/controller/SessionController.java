package com.security.controller;

import com.security.service.CryptoService;
import com.security.service.SessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * 会话控制器
 * 处理加密会话的建立和管理
 */
@RestController
@RequestMapping("/session")
@RequiredArgsConstructor
@Slf4j
public class SessionController {

    private final SessionService sessionService;
    private final CryptoService cryptoService;
    
    /**
     * 初始化加密会话
     * @param request 包含用户ID、加密会话密钥和IV的请求
     * @param response HTTP响应对象，用于设置Cookie
     * @return 会话初始化结果
     */
    @PostMapping("/init")
    public ResponseEntity<Map<String, Object>> initSession(
            @RequestBody SessionInitRequest request,
            HttpServletResponse response) {
        
        log.debug("用户{}请求初始化加密会话", request.getUserId());
        
        // 初始化会话
        String sessionId = sessionService.initSession(
                request.getUserId(),
                request.getEncryptedSessionKey(),
                request.getIv());
                
        // 关联用户与会话ID
        cryptoService.associateUserWithSession(request.getUserId(), sessionId);
        
        // 设置会话Cookie
        Cookie sessionCookie = new Cookie("SESSION_ID", sessionId);
        sessionCookie.setPath("/");
        sessionCookie.setHttpOnly(true);
        sessionCookie.setMaxAge(3600); // 1小时过期
        response.addCookie(sessionCookie);
        
        // 返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("sessionId", sessionId);
        
        return ResponseEntity.ok(result);
    }
    
    /**
     * 会话初始化请求
     */
    public static class SessionInitRequest {
        private String userId;
        private String encryptedSessionKey;
        private String iv;
        
        // Getters and Setters
        public String getUserId() {
            return userId;
        }
        
        public void setUserId(String userId) {
            this.userId = userId;
        }
        
        public String getEncryptedSessionKey() {
            return encryptedSessionKey;
        }
        
        public void setEncryptedSessionKey(String encryptedSessionKey) {
            this.encryptedSessionKey = encryptedSessionKey;
        }
        
        public String getIv() {
            return iv;
        }
        
        public void setIv(String iv) {
            this.iv = iv;
        }
    }
}
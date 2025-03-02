package com.security.controller;

import com.security.service.AuditLogService;
import com.security.service.CryptoService;
import com.security.service.DataWarehouseService;
import com.security.service.SessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 查询控制器
 * 处理加密身份证号查询请求
 */
@RestController
@RequestMapping("/query")
@RequiredArgsConstructor
@Slf4j
public class QueryController {

    private final SessionService sessionService;
    private final DataWarehouseService dataWarehouseService;
    private final AuditLogService auditLogService;
    private final CryptoService cryptoService;
    
    /**
     * 加密查询接口
     * @param request 包含加密数据的请求
     * @param httpRequest HTTP请求对象，用于获取Cookie和IP地址
     * @return 加密的查询结果
     */
    @PostMapping
    public ResponseEntity<Map<String, String>> query(
            @RequestBody EncryptedQueryRequest request,
            HttpServletRequest httpRequest) {
        
        // 获取会话ID
        String sessionId = getSessionIdFromCookie(httpRequest);
        if (sessionId == null) {
            log.warn("查询请求没有有效的会话ID");
            return ResponseEntity.badRequest().build();
        }
        
        // 获取会话信息
        SessionService.SessionInfo sessionInfo = sessionService.getSession(sessionId);
        if (sessionInfo == null) {
            log.warn("会话不存在或已过期: {}", sessionId);
            return ResponseEntity.badRequest().build();
        }
        
        // 获取用户ID和IP地址
        String userId = sessionInfo.getUserId();
        String ipAddress = getClientIpAddress(httpRequest);
        
        // 记录审计日志
        String traceId = auditLogService.logRequest(userId, "QUERY_ID_CARD", ipAddress);
        
        try {
            // 调用数据仓库服务进行查询
            String encryptedResult = dataWarehouseService.queryEncrypted(
                    request.getEncryptedData(),
                    sessionInfo.getSessionKey(),
                    sessionInfo.getIv(),
                    userId,
                    traceId);
            
            // 更新审计日志状态
            auditLogService.updateStatus(traceId, "SUCCESS", null);
            
            // 返回加密结果
            Map<String, String> response = new HashMap<>();
            response.put("encryptedResult", encryptedResult);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("查询处理失败", e);
            
            // 更新审计日志状态
            auditLogService.updateStatus(traceId, "FAILED", e.getMessage());
            
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * 从Cookie中获取会话ID
     * @param request HTTP请求
     * @return 会话ID，如果不存在则返回null
     */
    private String getSessionIdFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("SESSION_ID".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
    
    /**
     * 获取客户端IP地址
     * @param request HTTP请求
     * @return IP地址
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
    
    /**
     * 加密查询请求
     */
    public static class EncryptedQueryRequest {
        private String encryptedData;
        private String iv;
        
        // Getters and Setters
        public String getEncryptedData() {
            return encryptedData;
        }
        
        public void setEncryptedData(String encryptedData) {
            this.encryptedData = encryptedData;
        }
        
        public String getIv() {
            return iv;
        }
        
        public void setIv(String iv) {
            this.iv = iv;
        }
    }
}
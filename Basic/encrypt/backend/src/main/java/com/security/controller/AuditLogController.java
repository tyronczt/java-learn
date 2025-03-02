package com.security.controller;

import com.security.entity.AuditLog;
import com.security.service.AuditLogService;
import com.security.service.CryptoService;
import com.security.crypto.CryptoUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 审计日志控制器
 * 提供审计日志查询API
 */
@RestController
@RequestMapping("/audit-logs")
@RequiredArgsConstructor
@Slf4j
public class AuditLogController {

    private final AuditLogService auditLogService;
    private final CryptoService cryptoService;

    /**
     * 查询审计日志
     * 支持多种查询方式：用户ID、身份证号(精确/模糊)、时间范围、操作类型
     * @param params 查询参数
     * @return 审计日志列表和总数
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> queryAuditLogs(@RequestBody Map<String, Object> params) {
        log.info("接收到审计日志查询请求");
        
        // 获取分页参数
        int page = params.containsKey("page") ? Integer.parseInt(params.get("page").toString()) - 1 : 0;
        int pageSize = params.containsKey("pageSize") ? Integer.parseInt(params.get("pageSize").toString()) : 10;
        Pageable pageable = PageRequest.of(page, pageSize);
        
        List<AuditLog> logs;
        long total = 0;
        
        try {
            // 根据不同的查询类型执行相应的查询
            if (params.containsKey("userId")) {
                // 按用户ID查询
                String userId = params.get("userId").toString();
                logs = auditLogService.findByUserId(userId, pageable);
                total = auditLogService.countByUserId(userId);
            } else if (params.containsKey("encryptedIdCard")) {
                // 按身份证号查询（需要解密）
                String encryptedIdCard = params.get("encryptedIdCard").toString();
                String userId = params.containsKey("userId") ? params.get("userId").toString() : "";
                String idCardNumber = cryptoService.decryptData(userId, encryptedIdCard);
                
                String queryType = params.containsKey("idCardQueryType") ? 
                        params.get("idCardQueryType").toString() : "exact";
                
                if ("exact".equals(queryType)) {
                    logs = auditLogService.findByIdCardExact(idCardNumber, pageable);
                    total = auditLogService.countByIdCardExact(idCardNumber);
                } else {
                    logs = auditLogService.findByIdCardFuzzy(idCardNumber, pageable);
                    total = auditLogService.countByIdCardFuzzy(idCardNumber);
                }
            } else if (params.containsKey("startTime") && params.containsKey("endTime")) {
                // 按时间范围查询
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                LocalDateTime startTime = LocalDateTime.parse(params.get("startTime").toString(), formatter);
                LocalDateTime endTime = LocalDateTime.parse(params.get("endTime").toString(), formatter);
                
                logs = auditLogService.findByTimeRange(startTime, endTime, pageable);
                total = auditLogService.countByTimeRange(startTime, endTime);
            } else if (params.containsKey("operation")) {
                // 按操作类型查询
                String operation = params.get("operation").toString();
                logs = auditLogService.findByOperation(operation, pageable);
                total = auditLogService.countByOperation(operation);
            } else {
                // 无查询条件，返回空
                logs = new ArrayList<>();
            }
            
            // 构建响应数据
            Map<String, Object> response = new HashMap<>();
            response.put("logs", logs);
            response.put("total", total);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("查询审计日志失败", e);
//            return ResponseEntity.badRequest().body(Map.of("error", "查询失败: " + e.getMessage()));
            return ResponseEntity.badRequest().body(new HashMap<>());
        }
    }
}
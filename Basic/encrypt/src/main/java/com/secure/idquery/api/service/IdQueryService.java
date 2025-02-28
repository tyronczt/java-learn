package com.secure.idquery.api.service;

import com.secure.idquery.audit.service.AuditService;
import com.secure.idquery.common.model.EncryptedRequest;
import com.secure.idquery.common.model.EncryptedResponse;
import com.secure.idquery.warehouse.service.DataWarehouseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class IdQueryService {
    
    private final AuditService auditService;
    private final DataWarehouseService dataWarehouseService;
    
    public EncryptedResponse processQuery(EncryptedRequest request) {
        EncryptedResponse response = new EncryptedResponse();
        
        try {
            // 生成请求追踪ID
            String traceId = auditService.generateTraceId();
            response.setTraceId(traceId);
            
            // 记录审计日志
            auditService.logRequest(traceId, request.getUserId(), "ID_QUERY");
            
            // 传递加密身份证号到数仓服务
            String encryptedResult = dataWarehouseService.processQuery(
                    request.getEncryptedData(),
                    request.getUserId(),
                    traceId
            );
            
            // 设置响应
            if (encryptedResult != null) {
                response.setEncryptedData(encryptedResult);
                response.setSuccess(true);
                response.setMessage("查询成功");
            } else {
                response.setSuccess(false);
                response.setMessage("未找到匹配数据");
            }
            
            return response;
        } catch (Exception e) {
            log.error("处理查询请求失败", e);
            response.setSuccess(false);
            response.setMessage("处理查询请求失败: " + e.getMessage());
            return response;
        }
    }
}
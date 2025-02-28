package com.secure.idquery.api.controller;

import com.secure.idquery.api.service.IdQueryService;
import com.secure.idquery.common.model.EncryptedRequest;
import com.secure.idquery.common.model.EncryptedResponse;
import com.secure.idquery.key.service.KeyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class IdQueryController {
    
    private final KeyService keyService;
    private final IdQueryService idQueryService;
    
    @GetMapping("/key/{userId}")
    public ResponseEntity<String> getUserPublicKey(@PathVariable String userId) {
        try {
            String publicKey = keyService.getUserPublicKey(userId);
            return ResponseEntity.ok(publicKey);
        } catch (Exception e) {
            log.error("获取用户公钥失败", e);
            return ResponseEntity.badRequest().body("获取用户公钥失败: " + e.getMessage());
        }
    }
    
    @PostMapping("/session")
    public ResponseEntity<String> establishSession(@RequestBody EncryptedRequest request) {
        try {
            keyService.storeSessionKey(request.getUserId(), request.getEncryptedSessionKey(), request.getIv());
            return ResponseEntity.ok("会话建立成功");
        } catch (Exception e) {
            log.error("建立会话失败", e);
            return ResponseEntity.badRequest().body("建立会话失败: " + e.getMessage());
        }
    }
    
    @PostMapping("/query")
    public ResponseEntity<EncryptedResponse> queryPersonInfo(@RequestBody EncryptedRequest request) {
        try {
            EncryptedResponse response = idQueryService.processQuery(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("查询失败", e);
            EncryptedResponse errorResponse = new EncryptedResponse();
            errorResponse.setSuccess(false);
            errorResponse.setMessage("查询失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
}
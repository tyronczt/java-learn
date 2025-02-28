package com.secure.idquery.warehouse.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.secure.idquery.audit.service.AuditService;
import com.secure.idquery.common.model.UserInfo;
import com.secure.idquery.common.util.CryptoUtils;
import com.secure.idquery.key.service.KeyService;
import com.secure.idquery.warehouse.entity.PersonData;
import com.secure.idquery.warehouse.repository.PersonDataRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class DataWarehouseService {
    
    private final PersonDataRepository personDataRepository;
    private final KeyService keyService;
    private final AuditService auditService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @PostConstruct
    public void init() {
        // 初始化一些测试数据
        try {
            initTestData("110101199001011234", "张三", "北京市朝阳区", "13800138000", "zhangsan@example.com");
            initTestData("310101199002022345", "李四", "上海市浦东新区", "13900139000", "lisi@example.com");
            initTestData("440101199003033456", "王五", "广州市天河区", "13700137000", "wangwu@example.com");
        } catch (Exception e) {
            log.error("初始化测试数据失败", e);
        }
    }
    
    private void initTestData(String idNumber, String name, String address, String phone, String email) throws Exception {
        // 计算身份证哈希和模糊索引
        String idNumberHash = Base64.getEncoder().encodeToString(CryptoUtils.sha256Hash(idNumber.getBytes()));
        String fuzzyIndex = CryptoUtils.generateFuzzyIndex(idNumber);
        
        // 检查数据是否已存在
        if (personDataRepository.findByIdNumberHash(idNumberHash).isPresent()) {
            return;
        }
        
        // 创建用户信息对象
        UserInfo userInfo = new UserInfo();
        userInfo.setId(idNumberHash.substring(0, 8));
        userInfo.setName(name);
        userInfo.setAddress(address);
        userInfo.setPhone(phone);
        userInfo.setEmail(email);
        
        // 使用系统密钥加密用户数据 - 使用CBC模式而不是GCM模式
        String userData = objectMapper.writeValueAsString(userInfo);
        SecretKey systemKey = CryptoUtils.base64ToAESKey(keyService.getSessionKey("system"));
        byte[] iv = Base64.getDecoder().decode(keyService.getSessionIV("system"));
        
        // 使用AES-CBC加密而不是AES-GCM
        byte[] encryptedData = CryptoUtils.aesCbcEncrypt(userData.getBytes(), systemKey, iv);
        
        // 保存加密数据
        PersonData personData = new PersonData();
        personData.setIdNumberHash(idNumberHash);
        personData.setFuzzyIndex(fuzzyIndex);
        personData.setEncryptedData(Base64.getEncoder().encodeToString(encryptedData));
        personData.setCreatedAt(LocalDateTime.now());
        personData.setUpdatedAt(LocalDateTime.now());
        
        personDataRepository.save(personData);
    }
    
    public String processQuery(String encryptedIdNumber, String userId, String traceId) {
        try {
            // 获取用户会话密钥
            String sessionKeyBase64 = keyService.getSessionKey(userId);
            String ivBase64 = keyService.getSessionIV(userId);
            
            SecretKey sessionKey = CryptoUtils.base64ToAESKey(sessionKeyBase64);
            byte[] iv = Base64.getDecoder().decode(ivBase64);
            
            // 解密身份证号 - 使用 CBC 模式
            byte[] encryptedIdNumberBytes = Base64.getDecoder().decode(encryptedIdNumber);
            byte[] idNumberBytes = CryptoUtils.aesCbcDecrypt(encryptedIdNumberBytes, sessionKey, iv);
            String idNumber = new String(idNumberBytes);
            
            // 计算身份证哈希和模糊索引
            String idNumberHash = Base64.getEncoder().encodeToString(CryptoUtils.sha256Hash(idNumber.getBytes()));
            String fuzzyIndex = CryptoUtils.generateFuzzyIndex(idNumber);
            
            // 更新审计日志
            auditService.updateAuditLog(traceId, idNumberHash, fuzzyIndex);
            
            // 查询数据库
            Optional<PersonData> personDataOpt = personDataRepository.findByIdNumberHash(idNumberHash);
            
            if (personDataOpt.isPresent()) {
                PersonData personData = personDataOpt.get();
                
                // 使用用户会话密钥重新加密数据
                byte[] dataBytes = Base64.getDecoder().decode(personData.getEncryptedData());
                
                // 这里模拟数仓处理：先解密系统加密的数据，然后用用户会话密钥重新加密
                SecretKey systemKey = CryptoUtils.base64ToAESKey(keyService.getSessionKey("system"));
                byte[] systemIv = Base64.getDecoder().decode(keyService.getSessionIV("system"));
                byte[] decryptedData = CryptoUtils.aesCbcDecrypt(dataBytes, systemKey, systemIv);
                
                // 使用用户会话密钥加密结果 - 使用 CBC 模式
                byte[] reEncryptedData = CryptoUtils.aesCbcEncrypt(decryptedData, sessionKey, iv);
                
                // 完成审计日志
                auditService.completeAuditLog(traceId, "SUCCESS", "查询成功");
                
                return Base64.getEncoder().encodeToString(reEncryptedData);
            } else {
                // 完成审计日志
                auditService.completeAuditLog(traceId, "NOT_FOUND", "未找到匹配数据");
                return null;
            }
        } catch (Exception e) {
            log.error("处理查询请求失败", e);
            auditService.completeAuditLog(traceId, "ERROR", e.getMessage());
            throw new RuntimeException("处理查询请求失败", e);
        }
    }
}
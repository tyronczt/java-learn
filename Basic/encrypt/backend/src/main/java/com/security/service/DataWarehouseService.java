package com.security.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.security.crypto.CryptoUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * 数据仓库服务
 * 负责处理加密身份证号查询和数据处理
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class DataWarehouseService {

    private final AuditLogService auditLogService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    /**
     * 处理加密查询请求
     * @param encryptedIdCard 加密的身份证号
     * @param sessionKey 会话密钥
     * @param iv 初始化向量
     * @param userId 用户ID
     * @param traceId 追踪ID
     * @return 加密的查询结果
     */
    public String queryEncrypted(String encryptedIdCard, String sessionKey, String iv, String userId, String traceId) {
        try {
            // 解密身份证号
            String idCardNumber = CryptoUtils.decryptWithAES(sessionKey, iv, encryptedIdCard);
            log.debug("用户{}查询身份证号，追踪ID: {}", userId, traceId);
            
            // 更新审计日志中的身份证信息
            auditLogService.updateIdCardInfo(traceId, idCardNumber);
            
            // 查询数据并组装结果
            Map<String, Object> result = queryPersonInfo(idCardNumber, userId);
            
            // 将结果转换为JSON字符串
            String resultJson = objectMapper.writeValueAsString(result);
            
            // 使用会话密钥加密结果
            return CryptoUtils.encryptWithAES(sessionKey, iv, resultJson);
        } catch (Exception e) {
            log.error("处理加密查询失败", e);
            throw new RuntimeException("处理加密查询失败", e);
        }
    }
    
    /**
     * 查询人员信息
     * 这里使用模拟数据，实际应用中应该查询数据库
     * @param idCardNumber 身份证号
     * @param userId 用户ID
     * @return 人员信息
     */
    private Map<String, Object> queryPersonInfo(String idCardNumber, String userId) {
        // 模拟数据库查询，根据身份证号获取人员信息
        // 在实际应用中，这里应该使用盲索引查询加密数据库
        
        // 从身份证号中提取信息
        String birthDateStr = idCardNumber.substring(6, 14);
        int genderCode = Integer.parseInt(idCardNumber.substring(16, 17));
        
        // 解析出生日期
        LocalDate birthDate = LocalDate.parse(birthDateStr, DateTimeFormatter.ofPattern("yyyyMMdd"));
        
        // 计算年龄
        int age = Period.between(birthDate, LocalDate.now()).getYears();
        
        // 确定性别
        String gender = (genderCode % 2 == 0) ? "女" : "男";
        
        // 创建结果对象
        Map<String, Object> result = new HashMap<>();
        
        // 根据用户ID和身份证号生成确定性的模拟数据
        // 这确保同一用户查询同一身份证号总是得到相同结果
        // 但不同用户查询相同身份证号会得到不同结果（用户隔离）
        int hashCode = (userId + idCardNumber).hashCode();
        
        // 模拟人员基本信息
        result.put("idCard", idCardNumber);
        result.put("name", getRandomName(hashCode));
        result.put("gender", gender);
        result.put("birthDate", birthDate.format(DateTimeFormatter.ISO_DATE));
        result.put("age", age);
        result.put("address", getRandomAddress(hashCode));
        result.put("phone", getRandomPhone(hashCode));
        result.put("maritalStatus", age > 22 ? "已婚" : "未婚");
        result.put("education", getRandomEducation(hashCode));
        result.put("occupation", getRandomOccupation(hashCode));
        
        return result;
    }
    
    /**
     * 根据哈希码生成确定性的随机姓名
     */
    private String getRandomName(int hashCode) {
        String[] firstNames = {"张", "王", "李", "赵", "刘", "陈", "杨", "黄", "周", "吴"};
        String[] secondNames = {"伟", "芳", "娜", "秀英", "敏", "静", "强", "磊", "洋", "艳"};
        
        int firstNameIndex = Math.abs(hashCode % firstNames.length);
        int secondNameIndex = Math.abs((hashCode / 10) % secondNames.length);
        
        return firstNames[firstNameIndex] + secondNames[secondNameIndex];
    }
    
    /**
     * 根据哈希码生成确定性的随机地址
     */
    private String getRandomAddress(int hashCode) {
        String[] provinces = {"北京市", "上海市", "广东省", "江苏省", "浙江省"};
        String[] cities = {"朝阳区", "浦东新区", "广州市", "南京市", "杭州市"};
        String[] streets = {"人民路", "中山路", "解放大道", "建国路", "和平街"};
        
        int provinceIndex = Math.abs(hashCode % provinces.length);
        int cityIndex = Math.abs((hashCode / 10) % cities.length);
        int streetIndex = Math.abs((hashCode / 100) % streets.length);
        int number = Math.abs((hashCode / 1000) % 100) + 1;
        
        return provinces[provinceIndex] + cities[cityIndex] + streets[streetIndex] + number + "号";
    }
    
    /**
     * 根据哈希码生成确定性的随机电话号码
     */
    private String getRandomPhone(int hashCode) {
        String[] prefixes = {"138", "139", "158", "188", "199"};
        int prefixIndex = Math.abs(hashCode % prefixes.length);
        
        StringBuilder sb = new StringBuilder(prefixes[prefixIndex]);
        for (int i = 0; i < 8; i++) {
            sb.append(Math.abs((hashCode / (i + 1)) % 10));
        }
        
        return sb.toString();
    }
    
    /**
     * 根据哈希码生成确定性的随机学历
     */
    private String getRandomEducation(int hashCode) {
        String[] educations = {"高中", "大专", "本科", "硕士", "博士"};
        int index = Math.abs(hashCode % educations.length);
        return educations[index];
    }
    
    /**
     * 根据哈希码生成确定性的随机职业
     */
    private String getRandomOccupation(int hashCode) {
        String[] occupations = {"工程师", "教师", "医生", "律师", "会计", "销售", "管理人员", "学生"};
        int index = Math.abs(hashCode % occupations.length);
        return occupations[index];
    }
}
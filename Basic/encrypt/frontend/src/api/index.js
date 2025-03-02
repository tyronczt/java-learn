/**
 * API服务类
 * 处理与后端的所有通信，包括加密会话建立和数据传输
 */
import axios from 'axios';
import cryptoUtils from '../utils/crypto';

// 创建axios实例
const api = axios.create({
  baseURL: '',  // 移除baseURL，使用Vite配置的代理
  timeout: 10000,   // 设置超时时间
  headers: {
    'Content-Type': 'application/json'
  }
});

// 会话密钥和IV存储
let sessionKey = null;
let sessionIV = null;
let encryptedSessionKey = null;

export default {
  /**
   * 获取用户的RSA公钥
   * @param {string} userId 用户ID
   * @returns {Promise<string>} 公钥
   */
  async getUserPublicKey(userId) {
    const response = await api.get(`/keys/public/${userId}`);
    return response.data.publicKey;
  },
  
  /**
   * 初始化加密会话
   * @param {string} userId 用户ID
   * @returns {Promise<boolean>} 是否成功初始化
   */
  async initEncryptionSession(userId) {
    try {
      // 1. 获取用户公钥
      const publicKey = await this.getUserPublicKey(userId);
      
      // 2. 生成AES会话密钥和IV
      sessionKey = cryptoUtils.generateAESKey();
      sessionIV = cryptoUtils.generateIV();
      
      // 3. 使用RSA公钥加密会话密钥
      const encryptedSessionKey = cryptoUtils.encryptWithRSA(publicKey, sessionKey);
      
      // 4. 发送加密的会话密钥给服务器
      const response = await api.post('/session/init', {
        userId,
        encryptedSessionKey,
        iv: sessionIV
      });
      
      return response.data.success;
    } catch (error) {
      console.error('初始化加密会话失败:', error);
      throw error;
    }
  },
  
  /**
   * 发送加密查询请求
   * @param {string} idCardNumber 身份证号
   * @returns {Promise<Object>} 查询结果
   */
  async queryEncrypted(idCardNumber) {
    if (!sessionKey || !sessionIV) {
      throw new Error('加密会话未初始化');
    }
    
    try {
      // 1. 使用AES加密身份证号
      const encryptedIdCard = cryptoUtils.encryptWithAES(sessionKey, sessionIV, idCardNumber);
      
      // 2. 发送加密查询请求
      const response = await api.post('/query', {
        encryptedData: encryptedIdCard,
        iv: sessionIV
      });
      
      // 3. 解密返回的数据
      if (response.data && response.data.encryptedResult) {
        const decryptedData = cryptoUtils.decryptWithAES(
          sessionKey, 
          sessionIV, 
          response.data.encryptedResult
        );
        
        return JSON.parse(decryptedData);
      }
      
      return response.data;
    } catch (error) {
      console.error('加密查询失败:', error);
      throw error;
    }
  },
  
  /**
   * 查询审计日志
   * @param {Object} params 查询参数
   * @param {number} page 页码
   * @param {number} pageSize 每页记录数
   * @returns {Promise<Object>} 审计日志结果
   */
  async queryAuditLogs(params, page = 1, pageSize = 10) {
    try {
      // 对查询参数进行处理，创建深拷贝避免修改原始对象
      const processedParams = { ...params };
      
      // 处理身份证号查询类型的情况
      if (processedParams.idCardNumber) {
        // 如果有会话密钥，对敏感查询参数进行加密
        if (sessionKey && sessionIV) {
          // 保留查询类型参数，先存储为临时变量
          const queryType = processedParams.idCardQueryType || 'exact';
          
          // 加密身份证号
          processedParams.encryptedIdCard = cryptoUtils.encryptWithAES(
            sessionKey, 
            sessionIV, 
            processedParams.idCardNumber
          );
          
          // 删除明文参数
          delete processedParams.idCardNumber;
          
          // 设置查询类型参数
          processedParams.idCardQueryType = queryType;
        } else {
          // 如果没有加密会话，给出明确的错误
          console.error('查询审计日志失败: 加密会话未初始化');
          throw new Error('加密会话未初始化，无法安全查询身份证信息');
        }
      }
      
      // 添加分页参数
      const queryParams = {
        ...processedParams,
        page,
        pageSize
      };
      
      console.log('发送到后端的最终参数:', JSON.stringify(queryParams));
      
      // 发送查询请求
      const response = await api.post('/audit-logs', queryParams);
      
      return response.data;
    } catch (error) {
      console.error('查询审计日志失败:', error);
      throw error;
    }
  },
  
  /**
   * 获取当前会话信息
   * @returns {Object} 会话信息
   */
  getSessionInfo() {
    return {
      hasActiveSession: !!sessionKey,
      sessionKeyPreview: sessionKey ? `${sessionKey.substring(0, 3)}...` : null,
      ivPreview: sessionIV ? `${sessionIV.substring(0, 3)}...` : null
    };
  },
  
  /**
   * 清除会话信息
   */
  clearSession() {
    sessionKey = null;
    sessionIV = null;
    encryptedSessionKey = null;
  }
};
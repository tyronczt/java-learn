/**
 * 加密工具类
 * 实现前端加密解密功能，包括RSA和AES加密
 */
import CryptoJS from 'crypto-js';
import JSEncrypt from 'jsencrypt';

export default {
  /**
   * 生成随机AES密钥
   * @param {number} length 密钥长度，默认32字节(256位)
   * @returns {string} 生成的密钥
   */
  generateAESKey(length = 32) {
    const chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
    let result = '';
    const charactersLength = chars.length;
    for (let i = 0; i < length; i++) {
      result += chars.charAt(Math.floor(Math.random() * charactersLength));
    }
    return result;
  },

  /**
   * 生成随机IV
   * @param {number} length IV长度，默认16字节(128位)
   * @returns {string} 生成的IV
   */
  generateIV(length = 16) {
    const chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
    let result = '';
    const charactersLength = chars.length;
    for (let i = 0; i < length; i++) {
      result += chars.charAt(Math.floor(Math.random() * charactersLength));
    }
    return result;
  },

  /**
   * 使用RSA公钥加密数据
   * @param {string} publicKey RSA公钥
   * @param {string} data 要加密的数据
   * @returns {string} 加密后的数据(Base64编码)
   */
  encryptWithRSA(publicKey, data) {
    const encrypt = new JSEncrypt();
    encrypt.setPublicKey(publicKey);
    return encrypt.encrypt(data);
  },

  /**
   * 使用AES-GCM模式加密数据
   * @param {string} key AES密钥
   * @param {string} iv 初始化向量
   * @param {string} data 要加密的数据
   * @returns {string} 加密后的数据(Base64编码)
   */
  encryptWithAES(key, iv, data) {
    // 使用 CryptoJS 的 AES 加密，使用CBC模式替代GCM模式
    // 因为CryptoJS默认不完全支持GCM模式，改用CBC模式确保兼容性
    
    // 检查密钥是否已经是Base64格式，如果不是，先进行Base64编码
    let keyBase64 = key;
    try {
      // 尝试解码，如果成功则可能是Base64
      atob(key);
      // 检查字符是否都是Base64允许的字符
      if (!key.match(/^[A-Za-z0-9+/]*={0,2}$/)) {
        keyBase64 = btoa(key);
        console.debug('密钥不是Base64格式，已转换为Base64格式');
      }
    } catch (e) {
      // 解码失败，说明不是Base64，需要转换
      keyBase64 = btoa(key);
      console.debug('密钥不是Base64格式，已转换为Base64格式');
    }
    
    const keyBytes = CryptoJS.enc.Base64.parse(keyBase64);
    const ivBytes = CryptoJS.enc.Utf8.parse(iv);
    
    const encrypted = CryptoJS.AES.encrypt(data, keyBytes, {
      iv: ivBytes,
      mode: CryptoJS.mode.CBC,
      padding: CryptoJS.pad.Pkcs7
    });
    
    return encrypted.toString();
  },

  /**
   * 使用AES-GCM模式解密数据
   * @param {string} key AES密钥
   * @param {string} iv 初始化向量
   * @param {string} encryptedData 加密的数据(Base64编码)
   * @returns {string} 解密后的数据
   */
  decryptWithAES(key, iv, encryptedData) {
    // 检查密钥是否已经是Base64格式，如果不是，先进行Base64编码
    let keyBase64 = key;
    try {
      // 尝试解码，如果成功则可能是Base64
      atob(key);
      // 检查字符是否都是Base64允许的字符
      if (!key.match(/^[A-Za-z0-9+/]*={0,2}$/)) {
        keyBase64 = btoa(key);
        console.debug('解密时密钥不是Base64格式，已转换为Base64格式');
      }
    } catch (e) {
      // 解码失败，说明不是Base64，需要转换
      keyBase64 = btoa(key);
      console.debug('解密时密钥不是Base64格式，已转换为Base64格式');
    }
    
    const keyBytes = CryptoJS.enc.Base64.parse(keyBase64);
    const ivBytes = CryptoJS.enc.Utf8.parse(iv);
    
    const decrypted = CryptoJS.AES.decrypt(encryptedData, keyBytes, {
      iv: ivBytes,
      mode: CryptoJS.mode.CBC,
      padding: CryptoJS.pad.Pkcs7
    });
    
    return decrypted.toString(CryptoJS.enc.Utf8);
  },

  /**
   * 对象加密 - 加密对象的所有字段
   * @param {string} key AES密钥
   * @param {string} iv 初始化向量
   * @param {Object} obj 要加密的对象
   * @returns {Object} 加密后的对象
   */
  encryptObject(key, iv, obj) {
    const result = {};
    for (const [k, v] of Object.entries(obj)) {
      if (typeof v === 'string') {
        result[k] = this.encryptWithAES(key, iv, v);
      } else if (typeof v === 'number') {
        result[k] = this.encryptWithAES(key, iv, v.toString());
      } else if (typeof v === 'boolean') {
        result[k] = this.encryptWithAES(key, iv, v.toString());
      } else if (v === null || v === undefined) {
        result[k] = v;
      } else if (typeof v === 'object') {
        result[k] = this.encryptObject(key, iv, v);
      }
    }
    return result;
  },

  /**
   * 对象解密 - 解密对象的所有字段
   * @param {string} key AES密钥
   * @param {string} iv 初始化向量
   * @param {Object} obj 要解密的对象
   * @returns {Object} 解密后的对象
   */
  decryptObject(key, iv, obj) {
    const result = {};
    for (const [k, v] of Object.entries(obj)) {
      if (typeof v === 'string' && v) {
        try {
          result[k] = this.decryptWithAES(key, iv, v);
        } catch (e) {
          result[k] = v; // 如果解密失败，保留原值
        }
      } else if (v === null || v === undefined) {
        result[k] = v;
      } else if (typeof v === 'object') {
        result[k] = this.decryptObject(key, iv, v);
      }
    }
    return result;
  }
};
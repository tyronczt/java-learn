package com.security.crypto;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * 加密工具类
 * 提供RSA和AES-GCM加密解密功能
 */
@Slf4j
public class CryptoUtils {

    static {
        // 添加BouncyCastle作为安全提供者
        Security.addProvider(new BouncyCastleProvider());
    }

    private static final String RSA_ALGORITHM = "RSA";
    private static final String AES_GCM_ALGORITHM = "AES/GCM/NoPadding";
    private static final String AES_CBC_ALGORITHM = "AES/CBC/PKCS5Padding";
    private static final int GCM_TAG_LENGTH = 128; // GCM认证标签长度

    /**
     * 生成RSA密钥对
     *
     * @param keySize 密钥大小（位）
     * @return RSA密钥对
     */
    public static KeyPair generateRSAKeyPair(int keySize) {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(RSA_ALGORITHM);
            keyPairGenerator.initialize(keySize, new SecureRandom());
            return keyPairGenerator.generateKeyPair();
        } catch (Exception e) {
            log.error("生成RSA密钥对失败", e);
            throw new RuntimeException("生成RSA密钥对失败", e);
        }
    }

    /**
     * 使用RSA公钥加密数据
     *
     * @param publicKey 公钥
     * @param data      要加密的数据
     * @return 加密后的数据（Base64编码）
     */
    public static String encryptWithRSA(PublicKey publicKey, String data) {
        try {
            Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] encryptedBytes = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return Base64.encodeBase64String(encryptedBytes);
        } catch (Exception e) {
            log.error("RSA加密失败", e);
            throw new RuntimeException("RSA加密失败", e);
        }
    }

    /**
     * 使用RSA私钥解密数据
     *
     * @param privateKey    私钥
     * @param encryptedData 加密的数据（Base64编码）
     * @return 解密后的数据
     */
    public static String decryptWithRSA(PrivateKey privateKey, String encryptedData) {
        try {
            Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] decryptedBytes = cipher.doFinal(Base64.decodeBase64(encryptedData));
            return new String(decryptedBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("RSA解密失败", e);
            throw new RuntimeException("RSA解密失败", e);
        }
    }

    /**
     * 生成AES密钥
     *
     * @param keySize 密钥大小（位）
     * @return AES密钥（Base64编码）
     */
    public static String generateAESKey(int keySize) {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(keySize, new SecureRandom());
            SecretKey secretKey = keyGenerator.generateKey();
            return Base64.encodeBase64String(secretKey.getEncoded());
        } catch (Exception e) {
            log.error("生成AES密钥失败", e);
            throw new RuntimeException("生成AES密钥失败", e);
        }
    }

    /**
     * 使用AES-GCM模式加密数据
     *
     * @param key  AES密钥（Base64编码）
     * @param iv   初始化向量
     * @param data 要加密的数据
     * @return 加密后的数据（Base64编码）
     */
    public static String encryptWithAES(String key, String iv, String data) {
        try {
            // 检查key是否已经是Base64编码，如果不是，先进行Base64编码
            String base64Key = key;
            if (!isBase64(key)) {
                base64Key = Base64.encodeBase64String(key.getBytes(StandardCharsets.UTF_8));
                log.debug("密钥不是Base64格式，已转换为Base64格式");
            }
            
            // 优先使用CBC模式加密（与前端兼容）
            try {
                return encryptWithAES_CBC(base64Key, iv, data);
            } catch (Exception e) {
                log.debug("CBC模式加密失败，尝试GCM模式", e);
                // 如果CBC模式加密失败，回退到GCM模式
                byte[] keyBytes = Base64.decodeBase64(base64Key);
                byte[] ivBytes = iv.getBytes(StandardCharsets.UTF_8);
                SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
                GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, ivBytes);

                Cipher cipher = Cipher.getInstance(AES_GCM_ALGORITHM);
                cipher.init(Cipher.ENCRYPT_MODE, keySpec, gcmParameterSpec);

                byte[] encryptedBytes = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
                return Base64.encodeBase64String(encryptedBytes);
            }
        } catch (Exception e) {
            log.error("AES加密失败", e);
            throw new RuntimeException("AES加密失败", e);
        }
    }

    /**
     * 使用AES-GCM模式解密数据
     *
     * @param key          AES密钥（Base64编码）
     * @param iv           初始化向量
     * @param encryptedData 加密的数据（Base64编码）
     * @return 解密后的数据
     */
    public static String decryptWithAES(String key, String iv, String encryptedData) {
        try {
            // 检查key是否已经是Base64编码，如果不是，先进行Base64编码
            String base64Key = key;
            if (!isBase64(key)) {
                base64Key = Base64.encodeBase64String(key.getBytes(StandardCharsets.UTF_8));
                log.debug("解密时密钥不是Base64格式，已转换为Base64格式");
            }
            
            // 记录解密参数信息，帮助诊断问题
            log.debug("解密参数 - 密钥长度: {}, IV长度: {}, 加密数据长度: {}", 
                    base64Key.length(), iv.length(), encryptedData.length());
            
            // 首先尝试使用CBC模式解密（与前端兼容）
            try {
                return decryptWithAES_CBC(base64Key, iv, encryptedData);
            } catch (Exception e) {
                log.debug("CBC模式解密失败，尝试GCM模式", e);
                // 如果CBC模式解密失败，回退到GCM模式
                byte[] keyBytes = Base64.decodeBase64(base64Key);
                byte[] ivBytes = iv.getBytes(StandardCharsets.UTF_8);
                byte[] encryptedBytes = Base64.decodeBase64(encryptedData);

                // 记录解密参数详细信息
                log.debug("GCM模式解密 - 密钥字节长度: {}, IV字节长度: {}, 加密数据字节长度: {}", 
                        keyBytes.length, ivBytes.length, encryptedBytes.length);

                SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
                GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, ivBytes);

                Cipher cipher = Cipher.getInstance(AES_GCM_ALGORITHM);
                cipher.init(Cipher.DECRYPT_MODE, keySpec, gcmParameterSpec);

                byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
                return new String(decryptedBytes, StandardCharsets.UTF_8);
            }
        } catch (Exception e) {
            log.error("AES解密失败", e);
            throw new RuntimeException("AES解密失败", e);
        }
    }
    
    /**
     * 使用AES-CBC模式加密数据（与前端兼容）
     *
     * @param key  AES密钥（Base64编码）
     * @param iv   初始化向量
     * @param data 要加密的数据
     * @return 加密后的数据（Base64编码）
     */
    public static String encryptWithAES_CBC(String key, String iv, String data) {
        try {
            byte[] keyBytes = Base64.decodeBase64(key);
            byte[] ivBytes = iv.getBytes(StandardCharsets.UTF_8);

            SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
            javax.crypto.spec.IvParameterSpec ivParameterSpec = new javax.crypto.spec.IvParameterSpec(ivBytes);

            Cipher cipher = Cipher.getInstance(AES_CBC_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivParameterSpec);

            byte[] encryptedBytes = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return Base64.encodeBase64String(encryptedBytes);
        } catch (Exception e) {
            log.error("AES-CBC加密失败", e);
            throw new RuntimeException("AES-CBC加密失败", e);
        }
    }

    /**
     * 使用AES-CBC模式解密数据（与前端兼容）
     *
     * @param key          AES密钥（Base64编码）
     * @param iv           初始化向量
     * @param encryptedData 加密的数据（Base64编码）
     * @return 解密后的数据
     */
    public static String decryptWithAES_CBC(String key, String iv, String encryptedData) {
        try {
            byte[] keyBytes = Base64.decodeBase64(key);
            byte[] ivBytes = iv.getBytes(StandardCharsets.UTF_8);
            byte[] encryptedBytes = Base64.decodeBase64(encryptedData);

            SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
            javax.crypto.spec.IvParameterSpec ivParameterSpec = new javax.crypto.spec.IvParameterSpec(ivBytes);

            Cipher cipher = Cipher.getInstance(AES_CBC_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivParameterSpec);

            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
            return new String(decryptedBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("AES-CBC解密失败", e);
            throw new RuntimeException("AES-CBC解密失败", e);
        }
    }

    /**
     * 将Base64编码的公钥字符串转换为PublicKey对象
     *
     * @param publicKeyBase64 Base64编码的公钥
     * @return PublicKey对象
     */
    public static PublicKey parsePublicKey(String publicKeyBase64) {
        try {
            byte[] keyBytes = Base64.decodeBase64(publicKeyBase64);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
            return keyFactory.generatePublic(keySpec);
        } catch (Exception e) {
            log.error("解析公钥失败", e);
            throw new RuntimeException("解析公钥失败", e);
        }
    }

    /**
     * 将Base64编码的私钥字符串转换为PrivateKey对象
     *
     * @param privateKeyBase64 Base64编码的私钥
     * @return PrivateKey对象
     */
    public static PrivateKey parsePrivateKey(String privateKeyBase64) {
        try {
            byte[] keyBytes = Base64.decodeBase64(privateKeyBase64);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
            return keyFactory.generatePrivate(keySpec);
        } catch (Exception e) {
            log.error("解析私钥失败", e);
            throw new RuntimeException("解析私钥失败", e);
        }
    }

    /**
     * 计算字符串的SHA-256哈希值
     *
     * @param input 输入字符串
     * @return Base64编码的哈希值
     */
    public static String sha256Hash(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            return Base64.encodeBase64String(hashBytes);
        } catch (Exception e) {
            log.error("计算SHA-256哈希失败", e);
            throw new RuntimeException("计算SHA-256哈希失败", e);
        }
    }

    /**
     * 生成指定长度的随机字符串
     *
     * @param length 字符串长度
     * @return 随机字符串
     */
    public static String generateRandomString(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(chars.length());
            sb.append(chars.charAt(randomIndex));
        }
        return sb.toString();
    }
    
    /**
     * 检查字符串是否为Base64编码
     *
     * @param str 要检查的字符串
     * @return 是否为Base64编码
     */
    private static boolean isBase64(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        try {
            // 尝试解码，如果成功则可能是Base64
            Base64.decodeBase64(str);
            // 检查字符是否都是Base64允许的字符
            return str.matches("^[A-Za-z0-9+/]*={0,2}$");
        } catch (Exception e) {
            return false;
        }
    }
}
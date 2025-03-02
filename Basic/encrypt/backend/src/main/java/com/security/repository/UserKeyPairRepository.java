package com.security.repository;

import com.security.entity.UserKeyPair;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 用户密钥对存储库
 * 提供对用户密钥对的数据库操作
 */
@Repository
public interface UserKeyPairRepository extends JpaRepository<UserKeyPair, Long> {

    /**
     * 根据用户ID和过期时间查询有效的密钥对
     * @param userId 用户ID
     * @param expiryDate 过期时间
     * @return 用户密钥对
     */
    Optional<UserKeyPair> findByUserIdAndExpiryDateAfter(String userId, LocalDateTime expiryDate);

    /**
     * 查询所有未过期的密钥对
     * @param expiryDate 过期时间
     * @return 未过期的密钥对列表
     */
    List<UserKeyPair> findAllByExpiryDateAfter(LocalDateTime expiryDate);

    /**
     * 删除过期的密钥对
     * @param expiryDate 过期时间
     * @return 删除的记录数
     */
    @Modifying
    @Transactional
    long deleteByExpiryDateBefore(LocalDateTime expiryDate);
}
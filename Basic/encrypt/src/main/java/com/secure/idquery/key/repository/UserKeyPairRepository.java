package com.secure.idquery.key.repository;

import com.secure.idquery.key.entity.UserKeyPair;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserKeyPairRepository extends JpaRepository<UserKeyPair, String> {
    Optional<UserKeyPair> findByUserIdAndActiveTrue(String userId);
}
package com.secure.idquery.warehouse.repository;

import com.secure.idquery.warehouse.entity.PersonData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PersonDataRepository extends JpaRepository<PersonData, Long> {
    Optional<PersonData> findByIdNumberHash(String idNumberHash);
    
    List<PersonData> findByFuzzyIndexContaining(String fuzzyPattern);
}
package com.institute.repository;

import com.institute.model.Institution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InstitutionRepository extends JpaRepository<Institution, Long> {
    Institution findByUsername(String username);
    Institution findByDbName(String dbName);
    Optional<Institution> findByNameIgnoreCase(String name);
    Optional<Institution> findByEmail(String email);

}

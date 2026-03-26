package com.medis.hospital_system.adapters.out.persistence.repository;

import com.medis.hospital_system.adapters.out.persistence.entity.PatientEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PatientJpaRepository extends JpaRepository<PatientEntity, Long> {

    Optional<PatientEntity> findByUserId(Long userId);

    boolean existsByUserId(Long userId);

    void deleteByUserId(Long userId);

    @EntityGraph(attributePaths = "user")
    Page<PatientEntity> findAll(Pageable pageable);

    @EntityGraph(attributePaths = "user")
    @Query("""
    SELECT p FROM PatientEntity p
    JOIN p.user u
    WHERE LOWER(u.name) LIKE LOWER(CONCAT('%', :name, '%'))
""")
    Page<PatientEntity> findByName(@Param("name") String name, Pageable pageable);
}
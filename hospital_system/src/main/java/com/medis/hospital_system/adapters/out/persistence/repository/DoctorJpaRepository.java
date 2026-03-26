package com.medis.hospital_system.adapters.out.persistence.repository;

import com.medis.hospital_system.adapters.out.persistence.entity.DoctorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DoctorJpaRepository extends JpaRepository<DoctorEntity, Long> {

    Optional<DoctorEntity> findByUserId(Long userId);

    boolean existsByUserId(Long userId);

    // Fixed: specialization is a @ElementCollection, needs a JPQL JOIN to query it
    @Query("SELECT DISTINCT d FROM DoctorEntity d JOIN d.specialization s WHERE LOWER(s) = LOWER(:specialization)")
    List<DoctorEntity> findBySpecializationIgnoreCase(@Param("specialization") String specialization);

    // Fixed: DoctorEntity has no firstName field — name lives on the linked UserEntity
    @Query("SELECT d FROM DoctorEntity d WHERE LOWER(d.user.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<DoctorEntity> findByUserNameContainingIgnoreCase(@Param("name") String name);

    void deleteByUserId(Long userId);
}
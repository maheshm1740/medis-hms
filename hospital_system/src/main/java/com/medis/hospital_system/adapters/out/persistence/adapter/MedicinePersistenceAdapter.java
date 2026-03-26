package com.medis.hospital_system.adapters.out.persistence.adapter;

import com.medis.hospital_system.adapters.out.persistence.entity.MedicineEntity;
import com.medis.hospital_system.adapters.out.persistence.mapper.MedicineMapper;
import com.medis.hospital_system.adapters.out.persistence.repository.MedicineJpaRepository;
import com.medis.hospital_system.domain.model.Medicine;
import com.medis.hospital_system.domain.repository.MedicineRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class MedicinePersistenceAdapter implements MedicineRepository {

    private final MedicineJpaRepository medicineJpaRepository;

    public MedicinePersistenceAdapter(MedicineJpaRepository medicineJpaRepository) {
        this.medicineJpaRepository = medicineJpaRepository;
    }

    @Override
    public Medicine save(Medicine medicine) {
        return MedicineMapper.toDomain(
                medicineJpaRepository.save(MedicineMapper.toEntity(medicine))
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Medicine> findById(Long id) {
        return medicineJpaRepository.findById(id)
                .map(MedicineMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Medicine> findByName(String name) {
        return medicineJpaRepository.findByName(name)
                .map(MedicineMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByName(String name) {
        return medicineJpaRepository.existsByName(name);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Medicine> findAll() {
        return medicineJpaRepository.findAll()
                .stream()
                .map(MedicineMapper::toDomain)
                .toList();
    }

    @Override
    public List<Medicine> searchMedicines(String search) {

        List<MedicineEntity> entities = medicineJpaRepository.searchMedicines(search);

        return entities.stream()
                .map(MedicineMapper::toDomain)
                .toList();
    }
}
package com.medis.hospital_system.application.service;

import com.medis.hospital_system.application.command.AddMedicineCommand;
import com.medis.hospital_system.application.port.in.MedicineUseCase;
import com.medis.hospital_system.domain.exception.DuplicateResourceException;
import com.medis.hospital_system.domain.exception.ResourceNotFoundException;
import com.medis.hospital_system.domain.model.Medicine;
import com.medis.hospital_system.domain.repository.MedicineRepository;
import com.medis.hospital_system.infrastructure.cache.CacheNames;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class MedicineService implements MedicineUseCase {

    private final MedicineRepository medicineRepository;

    public MedicineService(MedicineRepository medicineRepository) {
        this.medicineRepository = medicineRepository;
    }

    @Override
    @CacheEvict(value = CacheNames.MEDICINES_ALL, allEntries = true)
    public Medicine addMedicine(AddMedicineCommand command) {
        if (medicineRepository.existsByName(command.name())) {
            throw new DuplicateResourceException("A medicine with this name already exists: " + command.name());
        }
        Medicine medicine = new Medicine(
                null,
                command.name(),
                command.manufacturer(),
                command.price()
        );
        return medicineRepository.save(medicine);
    }

    @Override
    @Cacheable(value = CacheNames.MEDICINES, key = "#id")
    public Medicine getMedicineById(Long id) {
        return medicineRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Medicine", id));
    }

    @Override
    @Cacheable(value = CacheNames.MEDICINES, key = "'name:' + #name.toLowerCase()")
    public Medicine getMedicineByName(String name) {
        return medicineRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("Medicine not found with name: " + name));
    }

    @Override
    public List<Medicine> getAllMedicines(String search) {

        if (search == null || search.isBlank()) {
            return medicineRepository.findAll();
        }

        return medicineRepository.searchMedicines(search);
    }

    @Override
    @Caching(
            put  = { @CachePut(value = CacheNames.MEDICINES, key = "#medicineId") },
            evict = {
                    @CacheEvict(value = CacheNames.MEDICINES_ALL, allEntries = true),
                    @CacheEvict(value = CacheNames.MEDICINES, key = "'name:' + #result.name.toLowerCase()")
            }
    )
    public Medicine updatePrice(Long medicineId, BigDecimal newPrice) {
        Medicine medicine = getMedicineById(medicineId);
        medicine.updatePrice(newPrice);
        return medicineRepository.save(medicine);
    }

    @Override
    @Caching(
            put  = { @CachePut(value = CacheNames.MEDICINES, key = "#medicineId") },
            evict = {
                    @CacheEvict(value = CacheNames.MEDICINES_ALL, allEntries = true),
                    @CacheEvict(value = CacheNames.MEDICINES, key = "'name:' + #result.name.toLowerCase()")
            }
    )
    public Medicine updateManufacturer(Long medicineId, String newManufacturer) {
        Medicine medicine = getMedicineById(medicineId);
        medicine.updateManufacturer(newManufacturer);
        return medicineRepository.save(medicine);
    }
}
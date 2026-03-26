package com.medis.hospital_system.application.port.in;

import com.medis.hospital_system.application.command.AddMedicineCommand;
import com.medis.hospital_system.domain.model.Medicine;

import java.math.BigDecimal;
import java.util.List;

public interface MedicineUseCase {

    Medicine addMedicine(AddMedicineCommand command);

    Medicine getMedicineById(Long id);

    Medicine getMedicineByName(String name);

    List<Medicine> getAllMedicines(String search);

    Medicine updatePrice(Long medicineId, BigDecimal newPrice);

    Medicine updateManufacturer(Long medicineId, String newManufacturer);

}
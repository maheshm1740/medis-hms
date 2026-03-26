package com.medis.hospital_system.application.port.in;

import com.medis.hospital_system.application.command.RegisterPatientCommand;
import com.medis.hospital_system.application.dto.PatientDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PatientUseCase {

    PatientDetails registerPatient(RegisterPatientCommand command);

    PatientDetails getPatientById(Long id);

    PatientDetails getPatientByUserId(Long userId);

    Page<PatientDetails> getAllPatients(String name, Pageable pageable);

    PatientDetails updateAddress(Long patientId, String newAddress);

    PatientDetails updateEmergencyContact(Long patientId, String newContact);

    PatientDetails getPatientByUserIdDirect(Long userId); // no cache
}
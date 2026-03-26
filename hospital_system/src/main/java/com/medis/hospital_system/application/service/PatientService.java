package com.medis.hospital_system.application.service;

import com.medis.hospital_system.application.command.RegisterPatientCommand;
import com.medis.hospital_system.application.dto.PatientDetails;
import com.medis.hospital_system.application.port.in.PatientUseCase;
import com.medis.hospital_system.domain.exception.DuplicateResourceException;
import com.medis.hospital_system.domain.exception.ResourceNotFoundException;
import com.medis.hospital_system.domain.model.Patient;
import com.medis.hospital_system.domain.repository.PatientRepository;
import com.medis.hospital_system.domain.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class PatientService implements PatientUseCase {

    private final PatientRepository patientRepository;
    private final UserRepository userRepository;

    public PatientService(PatientRepository patientRepository,
                          UserRepository userRepository) {
        this.patientRepository = patientRepository;
        this.userRepository = userRepository;
    }

    private PatientDetails toDetails(Patient patient) {
        var user = userRepository.findById(patient.getUserId())
                .orElseThrow(() -> ResourceNotFoundException.of("User", patient.getUserId()));
        return new PatientDetails(
                patient.getId(),
                patient.getUserId(),
                user.getName(),
                patient.getBloodGroup(),
                patient.getAddress(),
                patient.getEmergencyContact()
        );
    }

    @Override
    public PatientDetails registerPatient(RegisterPatientCommand command) {

        var user = userRepository.findByEmail(command.email())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        patientRepository.findByUserId(user.getId())
                .ifPresent(p -> {
                    throw new DuplicateResourceException("Patient already exists");
                });

        Patient patient = new Patient(
                null,
                user.getId(),
                command.bloodGroup(),
                command.address(),
                command.emergencyContact()
        );

        return toDetails(patientRepository.save(patient));
    }

    @Override
    public PatientDetails getPatientById(Long id) {
        return toDetails(patientRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Patient", id)));
    }

    @Override
    public PatientDetails getPatientByUserId(Long userId) {
        return toDetails(patientRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found for user id: " + userId)));
    }

    @Override
    public Page<PatientDetails> getAllPatients(String name, Pageable pageable) {
        return patientRepository.getAllPatients(name, pageable)
                .map(this::toDetails);
    }

    @Override
    public PatientDetails updateAddress(Long id, String address) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Patient", id));
        patient.updateAddress(address);
        return toDetails(patientRepository.save(patient));
    }

    @Override
    public PatientDetails updateEmergencyContact(Long id, String contact) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Patient", id));
        patient.updateEmergencyContact(contact);
        return toDetails(patientRepository.save(patient));
    }

    @Override
    public PatientDetails getPatientByUserIdDirect(Long userId) {
        return toDetails(patientRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Patient profile not found for user id: " + userId)));
    }
}
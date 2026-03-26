package com.medis.hospital_system.application.port.in;

import com.medis.hospital_system.application.command.RegisterDoctorCommand;
import com.medis.hospital_system.application.dto.DoctorDetails;

import java.util.List;

public interface DoctorUseCase {

    DoctorDetails registerDoctor(RegisterDoctorCommand command);

    DoctorDetails getDoctorById(Long id);

    DoctorDetails getDoctorByUserId(Long userId);

    List<DoctorDetails> getAllDoctors();

    List<DoctorDetails> getDoctorsBySpecialization(String specialization);

    DoctorDetails updateDepartment(Long doctorId, String newDepartment);

    DoctorDetails updateSpecialization(Long doctorId, List<String> newSpecialization);

    DoctorDetails getDoctorByUserIdDirect(Long userId); // no cache
}
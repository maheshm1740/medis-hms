package com.medis.hospital_system.application.service;

import com.medis.hospital_system.application.command.BookAppointmentCommand;
import com.medis.hospital_system.application.command.RescheduleAppointmentCommand;
import com.medis.hospital_system.application.dto.AppointmentDetails;
import com.medis.hospital_system.application.port.in.AppointmentUseCase;
import com.medis.hospital_system.domain.exception.ResourceNotFoundException;
import com.medis.hospital_system.domain.model.Appointment;
import com.medis.hospital_system.domain.model.AppointmentStatus;
import com.medis.hospital_system.domain.repository.AppointmentRepository;
import com.medis.hospital_system.domain.repository.DoctorRepository;
import com.medis.hospital_system.domain.repository.PatientRepository;
import com.medis.hospital_system.domain.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AppointmentService implements AppointmentUseCase {

    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final UserRepository userRepository;          // ← add

    public AppointmentService(AppointmentRepository appointmentRepository,
                              DoctorRepository doctorRepository,
                              PatientRepository patientRepository,
                              UserRepository userRepository) {           // ← add
        this.appointmentRepository = appointmentRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.userRepository = userRepository;             // ← add
    }

    // ← new helper: enriches Appointment with names
    private AppointmentDetails toDetails(Appointment appointment) {
        String doctorName = doctorRepository.findById(appointment.getDoctorId())
                .map(doctor -> userRepository.findById(doctor.getUserId())
                        .map(user -> user.getName())
                        .orElse("Unknown"))
                .orElse("Unknown");

        String patientName = patientRepository.findById(appointment.getPatientId())
                .map(patient -> userRepository.findById(patient.getUserId())
                        .map(user -> user.getName())
                        .orElse("Unknown"))
                .orElse("Unknown");

        return new AppointmentDetails(
                appointment.getId(),
                appointment.getPatientId(),
                patientName,
                appointment.getDoctorId(),
                doctorName,
                appointment.getAppointmentTime(),
                appointment.getStatus(),
                appointment.getNotes()
        );
    }

    @Override
    public AppointmentDetails bookAppointment(BookAppointmentCommand command) {
        // ... existing validation logic unchanged ...
        Appointment appointment = new Appointment(
                null,
                command.patientId(),
                command.doctorId(),
                command.appointmentTime(),
                AppointmentStatus.SCHEDULED,
                command.notes()
        );
        return toDetails(appointmentRepository.save(appointment));  // ← wrap
    }

    @Override
    public AppointmentDetails getAppointmentById(Long id) {
        return toDetails(appointmentRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Appointment", id)));
    }

    @Override
    public List<AppointmentDetails> getAppointmentsByDoctor(Long doctorId) {
        return appointmentRepository.findByDoctorId(doctorId)
                .stream().map(this::toDetails).toList();
    }

    @Override
    public List<AppointmentDetails> getAppointmentsByPatient(Long patientId) {
        return appointmentRepository.findByPatientId(patientId)
                .stream().map(this::toDetails).toList();
    }

    @Override
    public List<AppointmentDetails> getAppointmentsByStatus(AppointmentStatus status) {
        return appointmentRepository.findByStatus(status)
                .stream().map(this::toDetails).toList();
    }

    @Override
    public List<AppointmentDetails> getAllAppointments() {
        return appointmentRepository.findAll()
                .stream().map(this::toDetails).toList();
    }

    @Override
    public AppointmentDetails completeAppointment(Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> ResourceNotFoundException.of("Appointment", appointmentId));
        appointment.completeAppointment();
        return toDetails(appointmentRepository.save(appointment));
    }

    @Override
    public AppointmentDetails cancelAppointment(Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> ResourceNotFoundException.of("Appointment", appointmentId));
        appointment.cancelAppointment();
        return toDetails(appointmentRepository.save(appointment));
    }

    @Override
    public AppointmentDetails rescheduleAppointment(RescheduleAppointmentCommand command) {
        // ... existing validation logic unchanged ...
        Appointment appointment = appointmentRepository.findById(command.appointmentId())
                .orElseThrow(() -> ResourceNotFoundException.of("Appointment", command.appointmentId()));
        appointment.reschedule(command.newAppointmentTime());
        return toDetails(appointmentRepository.save(appointment));
    }
}
package com.medis.hospital_system.application.port.in;

import com.medis.hospital_system.application.command.BookAppointmentCommand;
import com.medis.hospital_system.application.command.RescheduleAppointmentCommand;
import com.medis.hospital_system.application.dto.AppointmentDetails;
import com.medis.hospital_system.domain.model.AppointmentStatus;

import java.util.List;

public interface AppointmentUseCase {

    AppointmentDetails bookAppointment(BookAppointmentCommand command);

    AppointmentDetails getAppointmentById(Long id);

    List<AppointmentDetails> getAppointmentsByDoctor(Long doctorId);

    List<AppointmentDetails> getAppointmentsByPatient(Long patientId);

    List<AppointmentDetails> getAppointmentsByStatus(AppointmentStatus status);

    List<AppointmentDetails> getAllAppointments();

    AppointmentDetails completeAppointment(Long appointmentId);

    AppointmentDetails cancelAppointment(Long appointmentId);

    AppointmentDetails rescheduleAppointment(RescheduleAppointmentCommand command);
}
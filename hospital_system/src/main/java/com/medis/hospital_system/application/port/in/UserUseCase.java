package com.medis.hospital_system.application.port.in;

import com.medis.hospital_system.application.command.RegisterUserCommand;
import com.medis.hospital_system.application.command.UpdatePasswordCommand;
import com.medis.hospital_system.application.command.UpdatePhoneCommand;
import com.medis.hospital_system.domain.model.User;
import org.springframework.data.domain.Page;

public interface UserUseCase {

    User registerUser(RegisterUserCommand command);

    User getUserById(Long id);

    User getUserByEmail(String email);

    User getUserByPhone(String phone);

    Page<User> getAllUsers(String search, int page, int size);

    User updatePhone(UpdatePhoneCommand command);

    User updatePassword(UpdatePasswordCommand command);

    boolean emailExists(String email);
}
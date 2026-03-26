package com.medis.hospital_system.adapters.out.persistence.mapper;

import com.medis.hospital_system.adapters.out.persistence.entity.UserEntity;
import com.medis.hospital_system.domain.model.User;

public final class UserMapper {

    private UserMapper() {}

    public static UserEntity toEntity(User user) {

        if (user == null) {
            return null;
        }

        UserEntity entity = new UserEntity();

        entity.setId(user.getId());
        entity.setName(user.getName());
        entity.setEmail(user.getEmail());
        entity.setPassword(user.getPassword());
        entity.setPhone(user.getPhone());
        entity.setRole(user.getRole());
        entity.setPasswordChanged(user.isPasswordChanged());

        return entity;
    }

    public static User toDomain(UserEntity entity) {

        if (entity == null) {
            return null;
        }

        return new User(
                entity.getId(),
                entity.getName(),
                entity.getEmail(),
                entity.getPassword(),
                entity.getPhone(),
                entity.getRole(),
                entity.isPasswordChanged()
        );
    }
}
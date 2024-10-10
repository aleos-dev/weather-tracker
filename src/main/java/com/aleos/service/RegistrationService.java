package com.aleos.service;

import com.aleos.model.UserPayload;
import com.aleos.model.entity.UserVerification;

import java.util.Optional;

public interface RegistrationService {

    Optional<UserVerification> register(UserPayload userDto);
}

package com.aleos.service;

import com.aleos.model.UserPayload;
import com.aleos.model.entity.UserVerificationToken;

public interface RegistrationService {

    UserVerificationToken register(UserPayload userDto);
}

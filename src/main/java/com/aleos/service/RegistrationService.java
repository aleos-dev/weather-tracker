package com.aleos.service;

import com.aleos.model.UserPayload;
import com.aleos.model.entity.UserVerificationToken;

/**
 * Service interface for handling user registration processes.
 * <p>
 * This interface provides methods to register a new user using the given user payload.
 * Upon successful registration, a user verification token is returned.
 */
public interface RegistrationService {

    UserVerificationToken register(UserPayload userDto);
}

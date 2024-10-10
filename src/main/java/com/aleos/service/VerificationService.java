package com.aleos.service;

import com.aleos.model.entity.User;

import java.util.Optional;
import java.util.UUID;

public interface VerificationService {

    Optional<User> verify(UUID token);

}

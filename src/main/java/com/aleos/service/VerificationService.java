package com.aleos.service;

import java.util.UUID;

/**
 * Interface for a service that verifies UUID tokens.
 * The implementation of this service is responsible for validating the tokens
 * to ensure that they correspond to a legitimate verification request.
 */
public interface VerificationService {

    boolean verify(UUID token);
}

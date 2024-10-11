package com.aleos.service;

import java.util.UUID;

public interface VerificationService {

    boolean verify(UUID token);

}

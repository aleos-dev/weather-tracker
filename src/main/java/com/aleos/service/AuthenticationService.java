package com.aleos.service;

import com.aleos.exception.AuthenticationException;
import com.aleos.security.core.Authentication;

public interface AuthenticationService {

    Authentication authenticate(String username, String password) throws AuthenticationException;
}

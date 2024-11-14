package com.aleos.security.encoder;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Implementation of the {@link PasswordEncoder} interface using the BCrypt hashing function.
 * <p>
 * This class provides methods for encoding passwords and verifying raw passwords against encoded ones
 * using BCrypt, a hashing algorithm designed to be computationally expensive in order to increase security.
 */
public class BCryptPasswordEncoder implements PasswordEncoder {

    @Override
    public String encode(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    @Override
    public boolean matches(String rawPassword, String encodedPassword) {
        return BCrypt.checkpw(rawPassword, encodedPassword);
    }
}

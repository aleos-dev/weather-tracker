package com.aleos.security.encoder;

/**
 * Strategy interface for password encryption.
 * <p>
 * Implementations are responsible for providing a secure mechanism
 * to encode raw passwords and to verify if raw passwords match
 * encoded ones.
 */
public interface PasswordEncoder {

    String encode(String rawPassword);

    boolean matches(String rawPassword, String encodedPassword);
}

package com.aleos.service;

import com.aleos.exception.context.AuthenticationException;
import com.aleos.repository.UserRepository;
import com.aleos.security.core.Authentication;
import com.aleos.security.core.AuthenticationToken;
import com.aleos.security.core.Role;
import com.aleos.security.core.SimpleGrantedAuthority;
import com.aleos.security.encoder.PasswordEncoder;
import lombok.AllArgsConstructor;


@AllArgsConstructor
public class BaseAuthenticationService implements AuthenticationService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(String username, String password) throws AuthenticationException {
        var user = userRepository.find(username)
                .orElseThrow(() -> new AuthenticationException("The user does not exist"));

        if (passwordEncoder.matches(password, user.getPassword())) {
            return new AuthenticationToken(username, password, new SimpleGrantedAuthority(Role.USER));
        }

        throw new AuthenticationException("The password is incorrect");
    }
}

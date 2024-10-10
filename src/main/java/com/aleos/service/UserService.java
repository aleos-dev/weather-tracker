package com.aleos.service;

import com.aleos.context.Properties;
import com.aleos.exception.context.AuthenticationException;
import com.aleos.model.UserPayload;
import com.aleos.model.entity.User;
import com.aleos.model.entity.UserVerification;
import com.aleos.repository.UserRepository;
import com.aleos.security.core.Authentication;
import com.aleos.security.core.AuthenticationToken;
import com.aleos.security.core.Role;
import com.aleos.security.core.SimpleGrantedAuthority;
import com.aleos.security.encoder.PasswordEncoder;
import lombok.AllArgsConstructor;

import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.Optional;
import java.util.UUID;


@AllArgsConstructor
public class UserService implements AuthenticationService, VerificationService, RegistrationService {

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

    @Override
    public Optional<UserVerification> register(UserPayload userDto) {
        // todo: should be atomic, add mapper
        Optional<User> founded = userRepository.find(userDto.getUsername());
        if (founded.isEmpty()) {

            User user = new User();
            user.setUsername(userDto.getUsername());
            user.setPassword(passwordEncoder.encode(userDto.getPassword()));
            user.setEmail(userDto.getEmail());

            userRepository.save(user);

            var token = createToken(user);
            userRepository.saveVerificationToken(token);
            return Optional.of(token);
        }

        return Optional.empty();
    }

    private UserVerification createToken(User user) {
        UserVerification userVerification = new UserVerification();
        userVerification.setUser(user);
        userVerification.setToken(UUID.randomUUID());
        userVerification.setExpirationDate(retrieveTokenExpiration());
        return userVerification;
    }

    private Instant retrieveTokenExpiration() {
        long defaultExpirationTime = 3600;
        Optional<String> value = Properties.get("registration.token.expiration.seconds");

        try {
            return Instant.now().plusSeconds(
                    value.map(Long::parseLong).orElse(defaultExpirationTime)
            );
        } catch (DateTimeParseException | NumberFormatException e) {
            return Instant.now().plusSeconds(defaultExpirationTime);
        }
    }

    @Override
    public Optional<User> verify(UUID token) {
        return userRepository.verify(token);
    }
}

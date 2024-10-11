package com.aleos.service;

import com.aleos.context.Properties;
import com.aleos.exception.context.AuthenticationException;
import com.aleos.model.UserPayload;
import com.aleos.model.entity.User;
import com.aleos.model.entity.UserVerificationToken;
import com.aleos.repository.UserRepository;
import com.aleos.security.core.Authentication;
import com.aleos.security.core.AuthenticationToken;
import com.aleos.security.core.Role;
import com.aleos.security.core.SimpleGrantedAuthority;
import com.aleos.security.encoder.PasswordEncoder;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;

import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.Optional;
import java.util.UUID;


@AllArgsConstructor
public class UserService implements AuthenticationService, VerificationService, RegistrationService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final ModelMapper mapper;

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
    public Optional<UserVerificationToken> register(UserPayload userPayload) {
        return userRepository.find(userPayload.getUsername()).isPresent()
                ? Optional.empty()
                : Optional.of(registerNewUser(userPayload));
    }

    private UserVerificationToken registerNewUser(UserPayload userPayload) {
        User user = mapper.map(userPayload, User.class);
        userRepository.save(user);

        UserVerificationToken token = createToken(user);
        userRepository.saveVerificationToken(token);

        return token;
    }

    private UserVerificationToken createToken(User user) {
        UserVerificationToken userVerificationToken = new UserVerificationToken();
        userVerificationToken.setUser(user);
        userVerificationToken.setToken(UUID.randomUUID());
        userVerificationToken.setExpirationDate(retrieveTokenExpiration());

        return userVerificationToken;
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

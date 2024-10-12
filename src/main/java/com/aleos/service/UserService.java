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
            if (user.isVerified()) {
                return new AuthenticationToken(username, password, new SimpleGrantedAuthority(Role.USER));
            }
            throw new AuthenticationException("The user is not verified. Check your email for further instructions.");
        }

        throw new AuthenticationException("The password is incorrect");
    }

    @Override
    public UserVerificationToken register(UserPayload userPayload) {
            User user = mapper.map(userPayload, User.class);
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            userRepository.save(user);

            UserVerificationToken token = createToken(user);
            userRepository.saveToken(token);

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
    public boolean verify(UUID token) {
        Optional<User> userOptional = userRepository.findByTokenUuid(token);
        userOptional.ifPresent(userRepository::activate);

        return userOptional.isPresent();
    }
}

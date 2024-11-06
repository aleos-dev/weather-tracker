package com.aleos.service;

import com.aleos.context.Properties;
import com.aleos.exception.context.AuthenticationException;
import com.aleos.model.UserPayload;
import com.aleos.model.dto.LocationWeatherResponse;
import com.aleos.model.entity.Location;
import com.aleos.model.entity.User;
import com.aleos.model.entity.UserVerificationToken;
import com.aleos.repository.UserRepository;
import com.aleos.security.core.Authentication;
import com.aleos.security.core.AuthenticationToken;
import com.aleos.security.core.Role;
import com.aleos.security.core.SimpleGrantedAuthority;
import com.aleos.security.encoder.PasswordEncoder;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;

import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@AllArgsConstructor
public class UserService implements AuthenticationService, VerificationService, RegistrationService {

    public static final long TOKEN_EXPIRATION_TIME = 3600;

    private final UserRepository userRepository;

    private final WeatherApiClient weatherApiClient;

    private final PasswordEncoder passwordEncoder;

    private final ModelMapper mapper;

    public List<LocationWeatherResponse> findWeatherForAllLocationsByUsername(String username) {
        log.info("Fetching weather for all locations of user: {}", username);

        return userRepository.fetchUserLocationData(username)
                .stream()
                .map(row -> {
                    var lat = (double) row[0];
                    var lon = (double) row[1];
                    var preferredName = (String) row[2];

                    log.debug("Fetching weather for location: {}, latitude: {}, longitude: {}", preferredName, lat, lon);
                    var weatherData = weatherApiClient.getWeatherByLocation(lat, lon);

                    return LocationWeatherResponse.of(preferredName, lon, lat, weatherData);

                }).toList();
    }

    public void setLocationForUser(String username, String locationName, Double lon, Double lat) {
        log.info("Setting location '{}' for user: {}, longitude: {}, latitude: {}", locationName, username, lon, lat);
        var location = new Location(locationName, lon, lat);
        userRepository.updateUserLocationByUserName(username, location);
        log.debug("Location set successfully for user: {}", username);
    }

    public void removeLocationForUser(String username, double lon, double lat) {
        log.info("Removing location for user: {}, longitude: {}, latitude: {}", username, lon, lat);
        var coordinates = new Location.Coordinates(lon, lat);
        userRepository.removeLocationByCoordinates(username, coordinates);
        log.debug("Location removed successfully for user: {}", username);
    }

    public void renameLocationByCoordinates(String username, double lon, double lat, String newLocationName) {
        log.info("Renaming location for user: {}, longitude: {}, latitude: {}, new name: {}", username, lon, lat, newLocationName);
        userRepository.renameLocationByCoordinates(username, lon, lat, newLocationName);
        log.debug("Location renamed successfully for user: {}", username);
    }

    @Override
    public Authentication authenticate(String username, String password) throws AuthenticationException {
        log.info("Authenticating user: {}", username);

        var user = userRepository.find(username)
                .orElseThrow(() -> new AuthenticationException("The user does not exist"));

        if (passwordEncoder.matches(password, user.getPassword())) {
            if (user.isVerified()) {
                log.info("Authentication successful for user: {}", username);
                return new AuthenticationToken(username, new SimpleGrantedAuthority(Role.USER));
            }
            log.warn("Authentication failed - user not verified: {}", username);
            throw new AuthenticationException("The user is not verified. Check your email for further instructions.");
        }
        log.warn("Authentication failed - incorrect password for user: {}", username);
        throw new AuthenticationException("The password is incorrect");
    }

    @Override
    public UserVerificationToken register(UserPayload userPayload) {
        log.info("Registering new user: {}", userPayload.getUsername());

        User user = mapper.map(userPayload, User.class);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);

        log.debug("User saved successfully: {}", userPayload.getUsername());

        UserVerificationToken token = createToken(user);
        userRepository.saveToken(token);

        log.info("Verification token generated and saved for user: {}", userPayload.getUsername());
        return token;
    }

    @Override
    public boolean verify(UUID token) {
        log.info("Verifying user with token: {}", token);

        Optional<User> userOptional = userRepository.findByTokenUuid(token);
        userOptional.ifPresent(userRepository::activate);

        return userOptional.isPresent();
    }

    private UserVerificationToken createToken(User user) {
        UserVerificationToken userVerificationToken = new UserVerificationToken();
        userVerificationToken.setUser(user);
        userVerificationToken.setToken(UUID.randomUUID());
        userVerificationToken.setExpirationDate(retrieveTokenExpiration());

        log.debug("Verification token created for user: {}, token expiration: {}", user.getUsername(), userVerificationToken.getExpirationDate());
        return userVerificationToken;
    }

    private Instant retrieveTokenExpiration() {
        Optional<String> value = Properties.get("registration.token.expiration.seconds");
        try {
            return Instant.now().plusSeconds(
                    value.map(Long::parseLong).orElse(TOKEN_EXPIRATION_TIME)
            );
        } catch (DateTimeParseException | NumberFormatException e) {
            return Instant.now().plusSeconds(TOKEN_EXPIRATION_TIME);
        }
    }
}

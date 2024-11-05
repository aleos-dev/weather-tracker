package com.aleos.util;

import com.aleos.context.listener.TestContextInitializer;
import com.aleos.model.UserPayload;
import com.aleos.model.entity.User;
import com.aleos.model.entity.UserVerificationToken;
import com.aleos.repository.UserRepository;
import com.aleos.service.UserService;

import java.util.Optional;
import java.util.UUID;

public final class DbUtil {

    private static final UserService userService = TestContextInitializer.getServiceLocator().getBean(UserService.class);
    public static final UserRepository userRepository = TestContextInitializer.getServiceLocator().getBean(UserRepository.class);

    private DbUtil() {
        throw new IllegalStateException("Utility class");
    }

    public static UserVerificationToken createNewUser(UserPayload payload) {
        return userService.register(payload);
    }

    public static void verifyUserByToken(UUID token) {
        userService.verify(token);
    }

    public static void setLocationForUser(String username, String locationName, double lon, double lat) {
        userService.setLocationForUser(username, locationName, lon, lat);
    }

    public static Optional<User> findUser(String username) {
        return userRepository.find(username);
    }
}

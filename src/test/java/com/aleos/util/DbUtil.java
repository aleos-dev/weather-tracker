package com.aleos.util;

import com.aleos.context.listener.TestContextInitializer;
import com.aleos.model.UserPayload;
import com.aleos.model.entity.UserVerificationToken;
import com.aleos.service.UserService;

import java.util.UUID;

public final class DbUtil {

    private static final UserService userService = TestContextInitializer.getServiceLocator().getBean(UserService.class);

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
}

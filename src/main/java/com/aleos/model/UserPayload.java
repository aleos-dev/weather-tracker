package com.aleos.model;

import com.aleos.model.annotation.RequestParam;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Represents the payload for a user containing essential information such as username,
 * password, and email. This class is immutable and is used for validating user input
 * data during user registration.
 */
@Getter
@RequiredArgsConstructor
public class UserPayload {

    @RequestParam("name")
    @NotNull
    @Size(min = 3, max = 50, message = "The name length must be between {min} and {max}.")
    private final String username;

    @NotNull
    @Size(min = 3, max = 10, message = "The password length must be between {min} and {max}.")
    private final String password;

    @NotNull
    @Email(message = "The email format is invalid.")
    private final String email;
}

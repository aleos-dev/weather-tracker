package com.aleos.model;

import com.aleos.model.annotation.RequestParam;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UserPayload {

    @RequestParam("name")
    @Size(min = 3, max = 50, message = "The name length must be between {min} and {max}.")
    private final String username;

    @Size(min = 3, max = 10, message = "The password length must be between {min} and {max}.")
    private final String password;

    @Email(message = "The email format is invalid.")
    private final String email;
}

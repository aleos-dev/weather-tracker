package com.aleos.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.NaturalId;

import java.util.Set;

import static jakarta.persistence.CascadeType.MERGE;
import static jakarta.persistence.CascadeType.PERSIST;


@Entity
@Table(name = "users")
@Getter
@Setter
public class User {
    private static final int USERNAME_MIN_LENGTH = 3;
    private static final int USERNAME_MAX_LENGTH = 10;
    private static final int PASSWORD_MIN_LENGTH = 3;
    private static final int PASSWORD_MAX_LENGTH = 16;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Size(min = USERNAME_MIN_LENGTH, max = USERNAME_MAX_LENGTH,
            message = "Username must be between {min} and {max} characters long.")
    @Column(nullable = false, unique = true)
    private String username;

    @Size(min = PASSWORD_MIN_LENGTH, max = PASSWORD_MAX_LENGTH,
            message = "Password must be between {min} and {max} characters long.")
    @Column(nullable = false)
    private String password;

    @Email(message = "Email should be valid.")
    @NaturalId
    @Column(nullable = false, unique = true)
    private String email;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {PERSIST, MERGE})
    @JoinColumn(name = "role_id", nullable = false)
    private AuthorizationRole role;

    private boolean verified;

    @ManyToMany(mappedBy = "users")
    private Set<Location> locations;

    public boolean equals(final Object o) {
        if (o == this) return true;
        return o instanceof User u && u.getUsername().equals(this.getUsername());
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        result = result * PRIME + (username == null ? 43 : username.hashCode());
        return result;
    }
}

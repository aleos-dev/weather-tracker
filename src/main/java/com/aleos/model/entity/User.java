package com.aleos.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.NaturalId;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import static jakarta.persistence.CascadeType.*;


@Entity
@Table(name = "users")
@Getter
@Setter
public class User implements Serializable {
    private static final int USERNAME_MIN_LENGTH = 3;
    private static final int USERNAME_MAX_LENGTH = 32;
    private static final int PASSWORD_MIN_LENGTH = 3;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(min = USERNAME_MIN_LENGTH, max = USERNAME_MAX_LENGTH,
            message = "Username must be between {min} and {max} characters long.")
    @NaturalId
    @Column(nullable = false, unique = true)
    private String username;

    @Size(min = PASSWORD_MIN_LENGTH, message = "Password length should be at least {min} chars.")
    @Column(nullable = false)
    private String password;

    @Email(message = "Email should be valid.")
    @Column(nullable = false, unique = true)
    private String email;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {PERSIST, MERGE})
    @JoinColumn(name = "role_id", nullable = false)
    private AuthorizationRole role;

    private boolean verified;

    @OneToMany(mappedBy = "userLocationId.user", cascade = ALL, orphanRemoval = true)
    private Set<UserLocation> userLocations = new HashSet<>();

    public void addUserLocation(Location location) {
        var assignedName = location.getName();
        UserLocation userLocation = new UserLocation(new UserLocationId(this, location), assignedName);
        userLocations.add(userLocation);
    }

    public void removeLocationByCoordinates(Location.Coordinates coordinates) {
        userLocations.removeIf(userLocation -> {
            if (userLocation.getUserLocationId().getLocation().getCoordinates().equals(coordinates)) {
                userLocation.setUserLocationId(null);
                return true;
            }
            return false;
        });
    }

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

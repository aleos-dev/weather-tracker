package com.aleos.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * The UserLocation class represents the mapping between a user and a location.
 * It is a JPA entity mapped to a "user_location" table in the database.
 * This class uses a composite key defined by the UserLocationId class.
 */
@Entity
@Table(
        name = "user_location",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "location_id"})
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserLocation implements Serializable {

    private static final int NAME_MIN_LENGTH = 1;
    private static final int NAME_MAX_LENGTH = 50;

    @EmbeddedId
    private UserLocationId userLocationId;

    @Column(name = "name")
    @NotBlank(message = "Location name cannot be null or blank.")
    @Size(min = NAME_MIN_LENGTH, max = NAME_MAX_LENGTH, message = "Name must be between {min} and {max} characters long.")
    private String locationName;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o instanceof UserLocation comparable) {
            return userLocationId.equals(comparable.userLocationId);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return userLocationId.hashCode();
    }
}

package com.aleos.model.entity;

import jakarta.persistence.Embeddable;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

/**
 * Represents the composite primary key for the UserLocation entity, defining a many-to-many
 * relationship between User and Location entities.
 * The class implements the Serializable interface to allow instances to be serialized.
 */
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class UserLocationId implements Serializable {

    @ManyToOne
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "fk_user_user_location"))
    private User user;

    @ManyToOne
    @JoinColumn(name = "location_id", foreignKey = @ForeignKey(name = "fk_location_user_location"))
    private Location location;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o instanceof UserLocationId that) {
            return Objects.equals(user, that.user) && Objects.equals(location, that.location);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, location);
    }
}

package com.aleos.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.NaturalId;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.GenerationType.IDENTITY;

/**
 * Class representing a geographic location defined by its name and coordinates.
 * This class is a JPA entity mapped to a database table with fields for name and coordinates.
 * It also maintains a set of associated user locations.
 */
@Entity
@Getter
@Table(
        indexes = {
                @Index(name = "location_idx_longitude_latitude", columnList = "longitude, latitude"),
        }
)
@NoArgsConstructor
@AllArgsConstructor
public class Location implements Serializable {

    private static final int NAME_MIN_LENGTH = 1;
    private static final int NAME_MAX_LENGTH = 50;

    public Location(String name, double lon, double lat) {
        this.name = name;
        this.coordinates = new Coordinates(lon, lat);
    }

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Embedded
    @NaturalId
    @NotNull
    @Setter(AccessLevel.NONE)
    private Coordinates coordinates;

    @Size(min = NAME_MIN_LENGTH, max = NAME_MAX_LENGTH, message = "Name must be between {min} and {max} characters long.")
    @Column(nullable = false)
    private String name;

    @OneToMany(mappedBy = "userLocationId.location", cascade = ALL, orphanRemoval = true)
    private Set<UserLocation> userLocations = new HashSet<>();

    @Override
    public boolean equals(final Object o) {
        if (o == this) return true;
        return o instanceof Coordinates cor && cor.equals(this.coordinates);
    }

    @Override
    public int hashCode() {
        return coordinates.hashCode();
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Embeddable
    public static class Coordinates implements Serializable {

        @Column(name = "longitude", nullable = false)
        private double lon;

        @Column(name = "latitude", nullable = false)
        private double lat;

        @Override
        public boolean equals(final Object o) {
            if (o == this) return true;
            return o instanceof Coordinates loc
                   && Double.compare(loc.lon, this.lon) == 0
                   && Double.compare(loc.lat, this.lat) == 0;
        }

        @Override
        public int hashCode() {
            return Objects.hash(lon, lat);
        }
    }
}

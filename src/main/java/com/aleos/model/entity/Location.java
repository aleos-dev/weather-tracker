package com.aleos.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.Objects;
import java.util.Set;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@Table(
        indexes = {
                @Index(name = "location_idx_longitude_latitude", columnList = "longitude, latitude"),
        }
)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Location {

    private static final int NAME_MIN_LENGTH = 1;
    private static final int NAME_MAX_LENGTH = 50;

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(name = "longitude", nullable = false)
    private double lon;

    @Column(name = "latitude", nullable = false)
    private double lat;

    @Size(min = NAME_MIN_LENGTH, max = NAME_MAX_LENGTH, message = "Name must be between {min} and {max} characters long.")
    @Column(nullable = false)
    private String name;

    @ManyToMany
    @JoinTable(
            name = "user_location",
            joinColumns = @JoinColumn(name = "location_id", foreignKey = @ForeignKey(name = "fk_location_user_location")),
            inverseJoinColumns = @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "fk_user_user_location"))
    )
    private Set<User> users;

    @Override
    public boolean equals(final Object o) {
        if (o == this) return true;
        return o instanceof Location loc
               && Double.compare(loc.lon, this.lon) == 0
               && Double.compare(loc.lat, this.lat) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(lon, lat);
    }
}

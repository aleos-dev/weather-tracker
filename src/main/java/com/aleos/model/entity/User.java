package com.aleos.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.NaturalId;


@Entity
@Table(name = "users")
@EqualsAndHashCode(of = "username")
@Getter
@Setter
public class User {
    private static final int USERNAME_MIN_LENGTH = 3;
    private static final int USERNAME_MAX_LENGTH = 10;

    private static final int PASSWORD_MIN_LENGTH = 3;
    private static final int PASSWORD_MAX_LENGTH = 10;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NaturalId
    @Min(USERNAME_MIN_LENGTH)
    @Max(USERNAME_MAX_LENGTH)
    @Column(unique = true, nullable = false)
    private String username;

    @Min(PASSWORD_MIN_LENGTH)
    @Max(PASSWORD_MAX_LENGTH)
    @Column(nullable = false)
    private String password;

    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    private AuthorizationRole role;
}

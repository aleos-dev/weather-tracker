package com.aleos.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.NaturalId;

import java.time.Instant;
import java.util.UUID;

/**
 * The UserVerificationToken class represents a token used to verify a user's email address or identity.
 * It is mapped to the "user_verification" table in the database.
 * Each token is associated with a specific user and has an expiration date.
 */
@Entity
@Table(name = "user_verification", indexes = {
        @Index(name = "hash_idx_verification_token", columnList = "verificationToken")
})
@Getter
@Setter
@EqualsAndHashCode(of = "token")
public class UserVerificationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NaturalId
    @Column(unique = true, nullable = false)
    private UUID token;

    @ManyToOne(fetch = FetchType.LAZY)
    @NotNull
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotNull
    @Column(nullable = false, updatable = false)
    private Instant expirationDate;
}

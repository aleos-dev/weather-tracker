package com.aleos.model.entity;

import com.aleos.security.core.Role;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.NaturalId;

import java.io.Serializable;

@Entity
@Table(name = "authorization_role")
@EqualsAndHashCode(of = "role")
@Getter
@Setter
public class AuthorizationRole implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NaturalId
    @Column(unique = true, nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;
}

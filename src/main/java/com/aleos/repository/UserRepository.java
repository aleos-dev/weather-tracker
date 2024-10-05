package com.aleos.repository;

import com.aleos.model.entity.User;
import jakarta.persistence.EntityManagerFactory;
import lombok.AllArgsConstructor;

import java.util.Optional;

@AllArgsConstructor
public class UserRepository {

    private final EntityManagerFactory emf;

    public Optional<User> find(String username) {
        return Optional.of(emf.createEntityManager().find(User.class, username));
    }
}

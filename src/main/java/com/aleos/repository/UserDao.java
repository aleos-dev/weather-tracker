package com.aleos.repository;

import com.aleos.model.entity.User;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;

import java.util.Optional;

public class UserDao extends CrudDao<User, Long> {

    public UserDao(EntityManagerFactory emf, Class<User> clazz) {
        super(emf, clazz);
    }

    public Optional<User> find(String username) {
        return callWithinTx(em -> {
            TypedQuery<User> query = em.createQuery("SELECT u FROM User u WHERE u.username = :username", clazz);
            query.setParameter("username", username);

            return query.getResultList().stream().findFirst();
        });
    }
}

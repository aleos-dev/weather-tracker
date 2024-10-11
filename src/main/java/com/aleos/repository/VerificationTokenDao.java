package com.aleos.repository;

import com.aleos.model.entity.User;
import com.aleos.model.entity.UserVerificationToken;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;

import java.util.Optional;
import java.util.UUID;

public class VerificationTokenDao extends CrudDao<UserVerificationToken, Long> {

    public VerificationTokenDao(EntityManagerFactory emf, Class<UserVerificationToken> clazz) {
        super(emf, clazz);
    }

    public Optional<User> findUserByUuid(UUID token) {
        return callWithinTx(em -> {
            TypedQuery<User> query = em.createQuery(
                    "SELECT u FROM UserVerificationToken v JOIN FETCH v.user u WHERE v.token = :token", User.class);
            query.setParameter("token", token);

            return query.getResultList().stream().findFirst();
        });
    }
}

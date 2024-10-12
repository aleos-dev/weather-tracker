package com.aleos.repository;

import com.aleos.model.entity.User;
import com.aleos.model.entity.UserVerificationToken;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public class VerificationTokenDao extends CrudDao<UserVerificationToken, Long> {

    public VerificationTokenDao(EntityManagerFactory emf, Class<UserVerificationToken> clazz) {
        super(emf, clazz);
    }

    public Optional<User> findUserByUuid(UUID token) {
        final String findVerificationTokenByUuid = """
                        SELECT vt
                        FROM UserVerificationToken vt join fetch vt.user
                        WHERE vt.token = :token AND vt.expirationDate > :currentTimestamp
                """;

        return callWithinTx(em -> {
            TypedQuery<UserVerificationToken> query = em.createQuery(findVerificationTokenByUuid, clazz);
            query.setParameter("token", token);
            query.setParameter("currentTimestamp", Instant.now());

            var foundToken = query.getResultList().stream().findFirst();
            foundToken.ifPresent(em::remove);

            return foundToken.map(UserVerificationToken::getUser);
        });
    }
}

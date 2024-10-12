package com.aleos.repository;

import com.aleos.exception.repository.DaoOperationException;
import com.aleos.exception.repository.UniqueConstraintViolationException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import org.hibernate.exception.ConstraintViolationException;

import java.util.function.Consumer;
import java.util.function.Function;

@RequiredArgsConstructor
public abstract class CrudDao<E, K> {

    protected final EntityManagerFactory emf;

    protected final Class<E> clazz;

    public void save(E entity) {
        runWithinTx(em -> em.persist(entity));
    }

    public <T> T callWithinTx(Function<EntityManager, T> emFunc) {
    try (var em = emf.createEntityManager()) {
        em.getTransaction().begin();
        try {
            T result = emFunc.apply(em);
            em.getTransaction().commit();
            return result;
        } catch (Exception e) {
            em.getTransaction().rollback();
            handleException(e);
            throw new DaoOperationException(e.getMessage() == null ? "Transaction is rolled back." : e.getMessage(), e);
        }
    }
    }

    public void runWithinTx(Consumer<EntityManager> emConsumer) {
        callWithinTx(em -> {
            emConsumer.accept(em);
            return null;
        });
    }

    private void handleException(Exception e) {
        if (e instanceof ConstraintViolationException cEx) {
            String errorMessage = "Unique constraint violation: %s".formatted(cEx.getConstraintName());
            throw new UniqueConstraintViolationException(errorMessage, e);
        }
    }
}

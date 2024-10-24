package com.aleos.repository;

import com.aleos.model.entity.Location;
import com.aleos.model.entity.User;
import com.aleos.model.entity.UserVerificationToken;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class UserRepository extends CrudDao<User> {

    private final VerificationTokenDao verificationTokenDao;

    public UserRepository(EntityManagerFactory emf, VerificationTokenDao verificationTokenDao) {
        super(emf, User.class);
        this.verificationTokenDao = verificationTokenDao;
    }

    public void updateUserLocationByUserName(String username, Location location) {
        runWithinTx(em -> {

            var user = findByUsername(username, em)
                    .orElseThrow();

            var foundedLocation = Optional.ofNullable(em.unwrap(Session.class)
                    .bySimpleNaturalId(Location.class)
                    .load(location.getCoordinates()));

            foundedLocation.ifPresentOrElse(
                    user::addUserLocation,
                    () -> {
                        em.persist(location);
                        user.addUserLocation(location);
                    });
        });

    }

    public void removeLocation(String username, Location location) {
        runWithinTx(em -> {

            var user = findByUsername(username, em);
            user.ifPresent(u -> u.removeLocation(location));
        });
    }

    public List<Object[]> fetchUserLocationData(String username) {
        String sql = """
                 SELECT l.longitude, l.latitude,
                        COALESCE(ul.name, l.name) AS name
                 FROM users u
                 JOIN user_location ul ON u.id = ul.user_id
                 JOIN location l ON ul.location_id = l.id
                 WHERE u.username = :username
                 ORDER BY l.id;
                """;

        return callWithinTx(em -> em.createNativeQuery(sql)
                .unwrap(Query.class)
                .setParameter("username", username)
                .stream().toList());
    }

    public void saveToken(UserVerificationToken token) {
        verificationTokenDao.save(token);
    }

    public Optional<User> findByTokenUuid(UUID token) {
        return verificationTokenDao.findUserByUuid(token);
    }

    public void activate(User user) {
        runWithinTx(em -> {
            var merged = em.merge(user);
            merged.setVerified(true);
        });
    }

    public Optional<User> find(String username) {
        return callWithinTx(em -> findByUsername(username, em));
    }

    private Optional<User> findByUsername(String username, EntityManager em) {
        return Optional.ofNullable(em.unwrap(Session.class).bySimpleNaturalId(User.class)
                .load(username));
    }

}

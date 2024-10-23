package com.aleos.repository;

import com.aleos.model.entity.Location;
import com.aleos.model.entity.User;
import com.aleos.model.entity.UserVerificationToken;
import jakarta.persistence.EntityManager;
import lombok.AllArgsConstructor;
import org.hibernate.query.Query;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;


@AllArgsConstructor
public class UserRepository {

    private final UserDao userDao;
    private final VerificationTokenDao verificationTokenDao;

    public void save(User user) {
        userDao.save(user);
    }

    public void addLocationToUser(String username, Location location) {
        userDao.runWithinTx(em -> {
            User user = em.getReference(User.class, username);
            updateUserLocation(location, user, em);
        });
    }

    public void removeLocationFromUser(String username, Location location) {
        userDao.runWithinTx(em -> {
            User user = em.getReference(User.class, username);
            user.getLocations().remove(location);
        });
    }

    public Stream<Object[]> streamUserLocationsData(String username) {
        String sql = """
                 SELECT l.longitude, l.latitude,
                        COALESCE(ul.name, l.name) AS name
                 FROM users u
                 JOIN user_location ul ON u.id = ul.user_id
                 JOIN location l ON ul.location_id = l.id
                 WHERE u.username = :username
                 ORDER BY l.id;
                """;

        return userDao.callWithinTx(em -> em.createNativeQuery(sql)
                .unwrap(Query.class)
                .setParameter("username", username)
                .setMaxResults(5)
                .stream());
    }

    public void saveToken(UserVerificationToken token) {
        verificationTokenDao.save(token);
    }

    public Optional<User> find(String username) {
        return userDao.find(username);
    }


    public Optional<User> findByTokenUuid(UUID token) {
        return verificationTokenDao.findUserByUuid(token);
    }

    public void activate(User user) {
        userDao.runWithinTx(em -> {
            var merged = em.merge(user);
            merged.setVerified(true);
        });
    }

    private void updateUserLocation(Location location, User user, EntityManager em) {
        Location mergedLocation = em.merge(location);
        user.getLocations().add(mergedLocation);
    }

}

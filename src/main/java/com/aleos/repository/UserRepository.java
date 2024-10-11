package com.aleos.repository;

import com.aleos.model.entity.User;
import com.aleos.model.entity.UserVerificationToken;
import lombok.AllArgsConstructor;

import java.util.Optional;
import java.util.UUID;

@AllArgsConstructor
public class UserRepository {

    private final UserDao userDao;
    private final VerificationTokenDao verificationTokenDao;

    public void save(User user) {
        userDao.save(user);
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
}

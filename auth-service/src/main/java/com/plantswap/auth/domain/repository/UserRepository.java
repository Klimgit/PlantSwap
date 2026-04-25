package com.plantswap.auth.domain.repository;

import com.plantswap.auth.domain.model.Email;
import com.plantswap.auth.domain.model.User;
import com.plantswap.auth.domain.model.UserId;
import com.plantswap.auth.domain.model.Username;

import java.util.Optional;

/**
 * Driven port
 */
public interface UserRepository {

    void save(User user);

    Optional<User> findById(UserId id);

    Optional<User> findByEmail(Email email);

    Optional<User> findByUsername(Username username);

    boolean existsByEmail(Email email);

    boolean existsByUsername(Username username);
}

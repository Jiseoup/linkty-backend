package com.linkty.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.linkty.entities.postgresql.User;

public interface UserRepository extends JpaRepository<User, Long> {
    // Find a User entity by its email.
    Optional<User> findByEmail(String email);

    // Find a not deleted User entity by its email.
    Optional<User> findByEmailAndDeletedFalse(String email);

    // Check if a not deleted User entity exists with the given email.
    boolean existsByEmailAndDeletedFalse(String email);
}

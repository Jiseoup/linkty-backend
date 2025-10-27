package com.linkty.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.linkty.entities.postgresql.User;

public interface UserRepository extends JpaRepository<User, Long> {
    // Find a User entity by its email.
    Optional<User> findByEmail(String email);

    // Check if User entity exists with the given email.
    boolean existsByEmail(String email);
}

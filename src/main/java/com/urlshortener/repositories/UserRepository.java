package com.urlshortener.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.urlshortener.entities.User;

public interface UserRepository extends JpaRepository<User, Long> {
    // Check if a User entity exists with the given email.
    boolean existsByEmail(String email);

    // Find not deleted User entity by its email.
    Optional<User> findByEmailAndDeletedFalse(String email);
}

package com.urlshortener.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.urlshortener.entities.User;

public interface UserRepository extends JpaRepository<User, Long> {
    // Check if a User entity exists with the given email.
    boolean existsByEmail(String email);
}

package com.urlshortener.repositories;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.urlshortener.entities.RefreshToken;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {
    Optional<RefreshToken> findByEmail(String email);
}

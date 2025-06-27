package com.urlshortener.repositories;

import org.springframework.data.repository.CrudRepository;

import com.urlshortener.entities.RefreshToken;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {
}

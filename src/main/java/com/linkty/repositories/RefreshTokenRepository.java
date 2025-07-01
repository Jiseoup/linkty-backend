package com.linkty.repositories;

import org.springframework.data.repository.CrudRepository;

import com.linkty.entities.RefreshToken;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {
}

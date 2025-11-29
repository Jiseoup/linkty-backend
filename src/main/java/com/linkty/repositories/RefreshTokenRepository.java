package com.linkty.repositories;

import org.springframework.data.repository.CrudRepository;

import com.linkty.entities.redis.RefreshToken;

public interface RefreshTokenRepository
        extends CrudRepository<RefreshToken, Long> {
}

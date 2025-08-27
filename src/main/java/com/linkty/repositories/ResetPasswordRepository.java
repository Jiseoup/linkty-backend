package com.linkty.repositories;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.linkty.entities.redis.ResetPassword;

public interface ResetPasswordRepository
        extends CrudRepository<ResetPassword, String> {
    // Find a ResetPassword entity by its hashToken.
    Optional<ResetPassword> findByHashToken(String hashToken);

    // Check if ResetPassword entity exists with the given hashToken.
    boolean existsByHashToken(String hashToken);
}

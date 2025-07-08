package com.linkty.repositories;

import org.springframework.data.repository.CrudRepository;

import com.linkty.entities.redis.EmailVerification;

public interface EmailVerificationRepository extends CrudRepository<EmailVerification, String> {
}

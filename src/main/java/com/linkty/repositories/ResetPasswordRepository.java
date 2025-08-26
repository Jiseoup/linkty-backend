package com.linkty.repositories;

import org.springframework.data.repository.CrudRepository;

import com.linkty.entities.redis.ResetPassword;

public interface ResetPasswordRepository
        extends CrudRepository<ResetPassword, String> {

}

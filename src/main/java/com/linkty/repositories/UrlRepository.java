package com.linkty.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.linkty.entities.postgresql.Url;

public interface UrlRepository extends JpaRepository<Url, Long> {
    // Find Url entity by its shortenUrl.
    Optional<Url> findByShortenUrl(String shortenUrl);
}

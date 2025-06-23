package com.urlshortener.domain;

import jakarta.persistence.*;
import java.time.ZonedDateTime;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "email", nullable = false, unique = true, columnDefinition = "TEXT")
    private String email;

    @Column(name = "password", nullable = false, length = 128)
    private String password;

    @Column(name = "join_date", nullable = false)
    private ZonedDateTime joinDate = ZonedDateTime.now();
}

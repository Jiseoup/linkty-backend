package com.urlshortener.domain;

import jakarta.persistence.*;
import java.time.ZonedDateTime;

@Entity
@Table(name = "urls")
public class Url {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = true, foreignKey = @ForeignKey(name = "fk_urls.user_id"))
    private User user;

    @Column(name = "original_url", nullable = false, columnDefinition = "TEXT")
    private String originalUrl;

    @Column(name = "shorten_url", nullable = false, length = 10, unique = true)
    private String shortenUrl;

    @Column(name = "click_count", nullable = false)
    private int clickCount = 0;

    @Column(name = "create_date", nullable = false)
    private ZonedDateTime createDate = ZonedDateTime.now();

    @Column(name = "expire_date", nullable = true)
    private ZonedDateTime expireDate;

    @Column(name = "deleted", nullable = false)
    private boolean deleted = false;
}

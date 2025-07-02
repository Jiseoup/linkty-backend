package com.linkty.entities;

import java.time.ZonedDateTime;

import lombok.Getter;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.AccessLevel;
import jakarta.persistence.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
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

    @Column(name = "alias", nullable = true, length = 20)
    private String alias;

    @Column(name = "original_url", nullable = false, columnDefinition = "TEXT")
    private String originalUrl;

    @Column(name = "shorten_url", nullable = false, columnDefinition = "CHAR(8)", length = 8, unique = true)
    private String shortenUrl;

    @Column(name = "active_date", nullable = true)
    private ZonedDateTime activeDate;

    @Column(name = "expire_date", nullable = true)
    private ZonedDateTime expireDate;

    @Column(name = "click_count", nullable = false)
    private int clickCount;

    @Column(name = "create_date", nullable = false)
    private ZonedDateTime createDate;

    @Column(name = "deleted", nullable = false)
    private boolean deleted;

    @PrePersist
    public void defaultValue() {
        createDate = ZonedDateTime.now();
        clickCount = 0;
        deleted = false;
    }

    // Increase the clickCount.
    public void increaseClickCount() {
        this.clickCount++;
    }
}

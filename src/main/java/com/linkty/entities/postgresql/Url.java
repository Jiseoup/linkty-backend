package com.linkty.entities.postgresql;

import java.util.List;
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
    @JoinColumn(name = "user_id", nullable = true,
            foreignKey = @ForeignKey(name = "fk_urls.user_id"))
    private User user;

    @Column(name = "alias", nullable = true, length = 20)
    private String alias;

    @Column(name = "original_url", nullable = false, columnDefinition = "TEXT")
    private String originalUrl;

    @Column(name = "shorten_url", nullable = false,
            columnDefinition = "CHAR(6)", length = 6, unique = true)
    private String shortenUrl;

    @Column(name = "active_date", nullable = true)
    private ZonedDateTime activeDate;

    @Column(name = "expire_date", nullable = true)
    private ZonedDateTime expireDate;

    @Column(name = "click_count", nullable = false)
    private int clickCount;

    @Column(name = "starred", nullable = false)
    private boolean starred;

    @Column(name = "active", nullable = false)
    private boolean active;

    @Column(name = "create_date", nullable = false)
    private ZonedDateTime createDate;

    @OneToMany(mappedBy = "url", cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<Log> logs;

    @PrePersist
    public void defaultValue() {
        clickCount = 0;
        active = true;
        createDate = ZonedDateTime.now();
    }

    // Increase the clickCount.
    public void increaseClickCount() {
        this.clickCount++;
    }

    // Toggle the active status.
    public void toggleActive() {
        this.active = !this.active;
    }
}

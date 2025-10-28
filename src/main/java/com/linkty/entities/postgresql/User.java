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
@Builder(toBuilder = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "email", nullable = false, columnDefinition = "TEXT",
            unique = true)
    private String email;

    @Column(name = "password", nullable = false, length = 128)
    private String password;

    @Column(name = "join_date", nullable = false)
    private ZonedDateTime joinDate;

    @Column(name = "last_login", nullable = true)
    private ZonedDateTime lastLogin;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<Url> urls;

    @PrePersist
    public void defaultValue() {
        joinDate = ZonedDateTime.now();
    }

    // Update last login timestamp.
    public void updateLastLogin() {
        this.lastLogin = ZonedDateTime.now();
    }

    // Change user password.
    public void changePassword(String password) {
        this.password = password;
    }
}

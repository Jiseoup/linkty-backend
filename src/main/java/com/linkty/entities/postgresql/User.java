package com.linkty.entities.postgresql;

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

    @Column(name = "deleted", nullable = false)
    private boolean deleted;

    @PrePersist
    public void defaultValue() {
        joinDate = ZonedDateTime.now();
        deleted = false;
    }

    // Set deleted field as true or false.
    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
}

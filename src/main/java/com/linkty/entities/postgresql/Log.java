package com.linkty.entities.postgresql;

import java.time.ZonedDateTime;

import jakarta.persistence.*;

@Entity
@Table(name = "logs")
public class Log {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "url_id", nullable = false, foreignKey = @ForeignKey(name = "fk_logs.url_id"))
    private Url url;

    @Column(name = "click_date", nullable = false)
    private ZonedDateTime clickDate = ZonedDateTime.now();

    @Column(name = "ip_address", nullable = false, length = 45)
    private String ipAddress;

    @Column(name = "client_info", nullable = false, columnDefinition = "TEXT")
    private String clientInfo;
}

package com.vivafit.vivafit.authentification.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter

@Entity
public class ConnectionDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;

    @Column(name = "ip_address", nullable = false, length = 25)
    private String ipAddress;

    @Column(name = "user_agent", nullable = false, length = 255)
    private String userAgent;

    @Column(name = "login_time", nullable = false, updatable = false)
    private LocalDateTime loginTime = LocalDateTime.now();

}

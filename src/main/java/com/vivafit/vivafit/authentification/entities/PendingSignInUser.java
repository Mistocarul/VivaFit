package com.vivafit.vivafit.authentification.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@RequiredArgsConstructor

@Entity
@Table(name = "pending_sign_in_users")
public class PendingSignInUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false, unique = true)
    private Integer id;

    @Column(name = "identifier", nullable = false, unique = true, length = 50)
    private String identifier;

    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @Column(name = "remember_browser", nullable = false, length = 5)
    private String rememberBrowser;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;
}

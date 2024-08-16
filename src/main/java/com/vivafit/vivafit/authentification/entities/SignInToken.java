package com.vivafit.vivafit.authentification.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor

@Entity
@Table(name = "sign_in_tokens")
public class SignInToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false, length = 255, unique = true)
    private Long id;

    @Column(name = "token", nullable = false, length = 255, unique = true)
    private String token;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @Column(name = "expiry_date", nullable = false, length = 255)
    private LocalDateTime expiryDate;
}

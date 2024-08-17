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
@Table(name = "confirmation_codes")
public class ConfirmationCode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    private Long id;

    @Column(nullable = false, unique = true, length = 50, name = "user_username")
    private String username;

    @Column(nullable = false, length = 6, name = "code")
    private int code;

    @Column(nullable = false, name = "creation_time")
    private LocalDateTime creationTime;
}

package com.vivafit.vivafit.authentification.entities;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor

@Entity
@Table(name = "pending_users")
public class PendingUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false, unique = true)
    private Integer id;

    @Column(name = "profile_picture", nullable = true, length = 255)
    private String profilePicture;

    @Column(name = "username", nullable = false, unique = true, length = 25)
    private String username;

    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @Column(name = "email", nullable = false, unique = true, length = 50)
    private String email;

    @Column(name = "phone_number", nullable = false, unique = true, length = 20)
    private String phoneNumber;

    @Column(name = "role", nullable = false, length = 20)
    private String role;
}

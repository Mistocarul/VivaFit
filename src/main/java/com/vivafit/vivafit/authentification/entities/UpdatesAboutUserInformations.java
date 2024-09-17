package com.vivafit.vivafit.authentification.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString

@Table(name = "updates_about_user_informations")
@Entity
public class UpdatesAboutUserInformations {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false, unique = true)
    private Integer id;

    @Column(name = "old_username", nullable = false, length = 25)
    private String oldUsername;

    @Column(name = "new_username", nullable = false, length = 25)
    private String newUsername;

    @Column(name = "old_email", nullable = false, length = 50)
    private String oldEmail;

    @Column(name = "new_email", nullable = false, length = 50)
    private String newEmail;

    @Column(name = "old_phone_number", nullable = false, length = 20)
    private String oldPhoneNumber;

    @Column(name = "new_phone_number", nullable = false, length = 20)
    private String newPhoneNumber;

    @Column(name = "old_password", nullable = false, length = 255)
    private String oldPassword;

    @Column(name = "new_password", nullable = false, length = 255)
    private String newPassword;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Date createdAt;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;
}

package com.vivafit.vivafit.specialist.entities;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

@Table(name = "messages_specialists")
@Entity
@Builder
public class MessagesSpecialists {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false, unique = true)
    private Integer id;

    @Column(name = "specialist_id", nullable = false)
    private Integer specialistId;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "user_complete_name", nullable = false, length = 100)
    private String userCompleteName;

    @Column(name = "user_email", nullable = false, length = 100)
    private String userEmail;

    @Column(name = "user_phone_number", nullable = false, length = 15)
    private String userPhoneNumber;

    @Column(name = "message", nullable = false, columnDefinition = "TEXT", length = 500)
    private String message;

    @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private String createdAt;
}

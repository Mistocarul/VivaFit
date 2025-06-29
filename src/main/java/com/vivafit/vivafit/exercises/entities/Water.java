package com.vivafit.vivafit.exercises.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

@Table(name = "water")
@Entity
public class Water {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false, unique = true)
    private Integer id;

    @Column(name = "amount", nullable = false)
    private Double amount; // in liters

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "date", nullable = false)
    private LocalDate date;
}

package com.vivafit.vivafit.exercises.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

@Table(name = "user_exercises")
@Entity
public class UserExercises {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false, unique = true)
    private Integer id;

    @Column(name = "exercise_id", nullable = true)
    private Integer exerciseId;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "exercise_date", nullable = false)
    private LocalDate date;

    @Column(name = "exercise_name", nullable = false)
    private String name;

    @Column(name = "exercise_description", nullable = true)
    private String description;

    @Column(name = "exercise_duration", nullable = false)
    private Integer duration;

    @Column(name = "exercise_calories_burned", nullable = false)
    private Double caloriesBurned;

    @Column(name = "exercise_category", nullable = false)
    private String category;
}

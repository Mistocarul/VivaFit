package com.vivafit.vivafit.exercises.entities;

import jakarta.persistence.*;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

@Table(name = "exercises")
@Entity
public class Exercises {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false, unique = true)
    private Integer id;

    @Column(name = "exercise_name", nullable = false)
    private String name;

    @Column(name = "exercise_description", nullable = false)
    private String description;

    @Column(name = "exercise_category", nullable = false)
    private String category;

    @Column(name = "exercise_duration", nullable = false)
    private Integer duration;

    @Column(name = "exercise_calories_burned", nullable = false)
    private Double caloriesBurned;
}

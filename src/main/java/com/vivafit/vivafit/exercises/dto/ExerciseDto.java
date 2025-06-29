package com.vivafit.vivafit.exercises.dto;

import lombok.*;

import java.time.LocalDate;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExerciseDto {
    private Integer userExerciseId;
    private Integer userId;
    private Integer exerciseId;
    private String name;
    private String description;
    private String category;
    private Integer duration;
    private Double caloriesBurned;
    private LocalDate date;
}

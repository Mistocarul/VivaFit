package com.vivafit.vivafit.exercises.services;

import com.vivafit.vivafit.exercises.dto.ExerciseDto;
import com.vivafit.vivafit.exercises.entities.Exercises;
import com.vivafit.vivafit.exercises.repositories.ExerciseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExerciseService {
    @Autowired
    private ExerciseRepository exerciseRepository;

    public List<ExerciseDto> getAllExercises() {
        List<Exercises> exercises = exerciseRepository.findAll();
        return exercises.stream().map(exercise -> ExerciseDto.builder()
                        .exerciseId(exercise.getId())
                        .name(exercise.getName())
                        .description(exercise.getDescription())
                        .category(exercise.getCategory())
                        .duration(exercise.getDuration())
                        .caloriesBurned(exercise.getCaloriesBurned())
                        .build())
                .collect(Collectors.toList());
    }
}

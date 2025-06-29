package com.vivafit.vivafit.exercises.services;

import com.vivafit.vivafit.exercises.dto.ExerciseDto;
import com.vivafit.vivafit.exercises.entities.UserExercises;
import com.vivafit.vivafit.exercises.repositories.UserExerciseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserExerciseService {
    @Autowired
    private UserExerciseRepository userExerciseRepository;

    public List<ExerciseDto> getAllExercisesByUser(Integer userId, LocalDate date) {
        return userExerciseRepository.findByUserId(userId)
                .orElse(List.of())
                .stream()
                .filter(ue -> ue.getUserId().equals(userId) && ue.getDate().equals(date))
                .map(ue -> ExerciseDto.builder()
                        .userExerciseId(ue.getId())
                        .userId(ue.getUserId())
                        .exerciseId(ue.getExerciseId())
                        .name(ue.getName())
                        .description(ue.getDescription())
                        .category(ue.getCategory())
                        .duration(ue.getDuration())
                        .caloriesBurned(ue.getCaloriesBurned())
                        .date(ue.getDate())
                        .build())
                .collect(Collectors.toList());
    }

    public ExerciseDto addExerciseToUser(ExerciseDto exerciseDto) {
        UserExercises userExercise = UserExercises.builder()
                .userId(exerciseDto.getUserId())
                .exerciseId(exerciseDto.getExerciseId())
                .name(exerciseDto.getName())
                .description(exerciseDto.getDescription())
                .category(exerciseDto.getCategory())
                .duration(exerciseDto.getDuration())
                .caloriesBurned(exerciseDto.getCaloriesBurned())
                .date(exerciseDto.getDate())
                .build();

        UserExercises savedUserExercise = userExerciseRepository.save(userExercise);

        return ExerciseDto.builder()
                .userExerciseId(savedUserExercise.getId())
                .userId(savedUserExercise.getUserId())
                .exerciseId(savedUserExercise.getExerciseId())
                .name(savedUserExercise.getName())
                .description(savedUserExercise.getDescription())
                .category(savedUserExercise.getCategory())
                .duration(savedUserExercise.getDuration())
                .caloriesBurned(savedUserExercise.getCaloriesBurned())
                .date(savedUserExercise.getDate())
                .build();
    }

    public ExerciseDto updateExerciseToUser(ExerciseDto exerciseDto) {
        UserExercises existingUserExercise = userExerciseRepository.findById(exerciseDto.getUserExerciseId())
                .orElseThrow(() -> new IllegalArgumentException("Exercise not found for the given ID"));

        existingUserExercise.setName(exerciseDto.getName());
        existingUserExercise.setDescription(exerciseDto.getDescription());
        existingUserExercise.setCategory(exerciseDto.getCategory());
        existingUserExercise.setDuration(exerciseDto.getDuration());
        existingUserExercise.setCaloriesBurned(exerciseDto.getCaloriesBurned());
        existingUserExercise.setDate(exerciseDto.getDate());

        UserExercises updatedUserExercise = userExerciseRepository.save(existingUserExercise);

        return ExerciseDto.builder()
                .userExerciseId(updatedUserExercise.getId())
                .userId(updatedUserExercise.getUserId())
                .exerciseId(updatedUserExercise.getExerciseId())
                .name(updatedUserExercise.getName())
                .description(updatedUserExercise.getDescription())
                .category(updatedUserExercise.getCategory())
                .duration(updatedUserExercise.getDuration())
                .caloriesBurned(updatedUserExercise.getCaloriesBurned())
                .date(updatedUserExercise.getDate())
                .build();
    }

    public void deleteExerciseFromUser(Integer userId, Integer userExerciseId) {
        UserExercises userExercise = userExerciseRepository.findById(userExerciseId)
                .orElseThrow(() -> new IllegalArgumentException("Exercise not found for the given ID"));

        if (!userExercise.getUserId().equals(userId)) {
            throw new IllegalArgumentException("You are not authorized to delete this exercise");
        }

        userExerciseRepository.delete(userExercise);
    }
}

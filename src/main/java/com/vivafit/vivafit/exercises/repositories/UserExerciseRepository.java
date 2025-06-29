package com.vivafit.vivafit.exercises.repositories;

import com.vivafit.vivafit.exercises.entities.UserExercises;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserExerciseRepository extends JpaRepository<UserExercises, Integer> {
    Optional<List<UserExercises>> findByUserId(Integer userId);
}

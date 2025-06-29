package com.vivafit.vivafit.exercises.repositories;

import com.vivafit.vivafit.exercises.entities.Exercises;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ExerciseRepository extends JpaRepository<Exercises, Integer> {
}

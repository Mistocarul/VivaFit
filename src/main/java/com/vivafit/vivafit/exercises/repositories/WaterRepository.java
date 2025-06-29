package com.vivafit.vivafit.exercises.repositories;

import com.vivafit.vivafit.exercises.entities.Water;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface WaterRepository extends JpaRepository<Water, Integer> {
    List<Water> findByUserIdAndDate(Integer userId, LocalDate date);
}

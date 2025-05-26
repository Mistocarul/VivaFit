package com.vivafit.vivafit.manage_calories.repositories;

import com.vivafit.vivafit.authentification.entities.User;
import com.vivafit.vivafit.manage_calories.entities.Meal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.tensorflow.op.Op;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface MealRepository extends JpaRepository<Meal, Integer> {
    List<Meal> findByUserId(Integer userId);
    List<Meal> findByUserIdAndDate(Integer userId, LocalDate date);
    Optional<Meal> findByDateAndUserAndMealType(LocalDate date, User user, String mealType);
    List<Meal> findByDateAndUser(LocalDate date, User user);
}

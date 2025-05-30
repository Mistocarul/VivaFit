package com.vivafit.vivafit.manage_calories.repositories;

import com.vivafit.vivafit.manage_calories.entities.MealFood;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MealFoodRepository  extends JpaRepository<MealFood, Integer> {
    List<MealFood> findByMealId(Integer mealId);
    List<MealFood> findByFoodId(Integer foodId);
    List<MealFood> findByFoodIdAndMealId(Integer foodId, Integer mealId);
    Optional<MealFood> findByMealIdAndFoodId(Integer mealId, Integer foodId);
}

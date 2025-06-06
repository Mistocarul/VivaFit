package com.vivafit.vivafit.manage_calories.repositories;

import com.vivafit.vivafit.manage_calories.entities.FoodFavorite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FoodFavoriteRepository extends JpaRepository<FoodFavorite, Long> {
    FoodFavorite findByUserIdAndFoodId(Integer userId, Integer foodId);
    List<FoodFavorite> findByUserId(Integer userId);
}

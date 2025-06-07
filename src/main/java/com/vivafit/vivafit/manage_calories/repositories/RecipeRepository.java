package com.vivafit.vivafit.manage_calories.repositories;

import com.vivafit.vivafit.manage_calories.entities.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RecipeRepository extends JpaRepository<Recipe, Integer> {
    Optional<List<Recipe>> findByUserId(Integer userId);
    Optional<Recipe> findByNameAndUserId(String name, Integer userId);
    List<Recipe> findByNameContainingIgnoreCase(String name);
}

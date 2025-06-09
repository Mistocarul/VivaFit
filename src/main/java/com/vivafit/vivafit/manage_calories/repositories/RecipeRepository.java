package com.vivafit.vivafit.manage_calories.repositories;

import com.vivafit.vivafit.manage_calories.entities.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RecipeRepository extends JpaRepository<Recipe, Integer> {
    Optional<List<Recipe>> findByUserId(Integer userId);
    Optional<Recipe> findByNameAndUserId(String name, Integer userId);
    List<Recipe> findByNameContainingIgnoreCase(String name);

    @Query("SELECT r FROM Recipe r WHERE r.id > :id ORDER BY r.id ASC LIMIT 30")
    List<Recipe> findByIdGreaterThanOrderByIdAsc(@Param("id") Integer id);
}

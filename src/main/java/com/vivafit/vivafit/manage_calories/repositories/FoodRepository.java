package com.vivafit.vivafit.manage_calories.repositories;

import com.vivafit.vivafit.manage_calories.entities.Food;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FoodRepository extends JpaRepository<Food, Integer> {
    List<Food> findByNameContainingIgnoreCase(String name);
    List<Food> findByBarcodeContainingIgnoreCase(String barcode);
    Optional<Food> findByNameIgnoreCase(String name);
}

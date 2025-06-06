package com.vivafit.vivafit.manage_calories.services;

import com.vivafit.vivafit.manage_calories.dto.FoodDto;
import com.vivafit.vivafit.manage_calories.entities.Food;
import com.vivafit.vivafit.manage_calories.entities.FoodFavorite;
import com.vivafit.vivafit.manage_calories.repositories.FoodFavoriteRepository;
import com.vivafit.vivafit.manage_calories.repositories.FoodRepository;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Getter
@Setter
@Service
public class FoodService {

    @Autowired
    private FoodRepository foodRepository;
    @Autowired
    private FoodFavoriteRepository foodFavoriteRepository;

    public Food createFood(FoodDto foodDto) {
        Food food = new Food();
        food.setBarcode(foodDto.getBarcode());
        food.setName(foodDto.getName());
        food.setCaloriesPer100g(foodDto.getCaloriesPer100g());
        food.setProteinPer100g(foodDto.getProteinPer100g());
        food.setFatPer100g(foodDto.getFatPer100g());
        food.setCarbsPer100g(foodDto.getCarbsPer100g());
        food.setCreatedBy(foodDto.getCreatedBy());

        Food savedFood = foodRepository.save(food);
        return savedFood;
    }

    public List<Food> getFoodsByName(String name) {
        return foodRepository.findByNameContainingIgnoreCase(name);
    }

    public List<Food> getFoodsByBarcode(String barcode) {
        return foodRepository.findByBarcodeContainingIgnoreCase(barcode);
    }

    public void deleteFoodById(Integer id) {
        Food food = foodRepository.findById(id).orElse(null);
        if (food != null) {
            foodRepository.delete(food);
        }
    }

    public Food getFoodById(Integer id) {
        return foodRepository.findById(id).orElse(null);
    }

    public Food updateFood(FoodDto foodDto) {
        Food food = foodRepository.findById(foodDto.getId()).orElse(null);
        if (food != null) {
            food.setBarcode(foodDto.getBarcode());
            food.setName(foodDto.getName());
            food.setCaloriesPer100g(foodDto.getCaloriesPer100g());
            food.setProteinPer100g(foodDto.getProteinPer100g());
            food.setFatPer100g(foodDto.getFatPer100g());
            food.setCarbsPer100g(foodDto.getCarbsPer100g());
            food.setCreatedBy(foodDto.getCreatedBy());
            return foodRepository.save(food);
        }
        return null;
    }

    public void addFoodToFavorites(Integer userId, Integer foodId) {
        FoodFavorite foodFavorite = new FoodFavorite();
        foodFavorite.setUserId(userId);
        foodFavorite.setFoodId(foodId);
        foodFavoriteRepository.save(foodFavorite);
    }

    public void removeFoodFromFavorites(Integer userId, Integer foodId) {
        FoodFavorite foodFavorite = foodFavoriteRepository.findByUserIdAndFoodId(userId, foodId);
        if (foodFavorite != null) {
            foodFavoriteRepository.delete(foodFavorite);
        }
    }

    public boolean isFoodFavorite(Integer userId, Integer foodId) {
        FoodFavorite foodFavorite = foodFavoriteRepository.findByUserIdAndFoodId(userId, foodId);
        if (foodFavorite != null) {
            return true;
        }
        return false;
    }

    public List<Food> getFavoriteFoods(Integer userId) {
        List<FoodFavorite> foodFavorites = foodFavoriteRepository.findByUserId(userId);
        return foodFavorites.stream()
                .map(foodFavorite -> foodRepository.findById(foodFavorite.getFoodId()).orElse(null))
                .filter(food -> food != null)
                .toList();
    }
}

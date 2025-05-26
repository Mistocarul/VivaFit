package com.vivafit.vivafit.manage_calories.services;

import com.vivafit.vivafit.manage_calories.dto.FoodDto;
import com.vivafit.vivafit.manage_calories.entities.Food;
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
}

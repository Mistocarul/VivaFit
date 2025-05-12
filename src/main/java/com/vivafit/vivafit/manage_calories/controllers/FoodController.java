package com.vivafit.vivafit.manage_calories.controllers;

import com.vivafit.vivafit.manage_calories.dto.FoodDto;
import com.vivafit.vivafit.manage_calories.entities.Food;
import com.vivafit.vivafit.manage_calories.responses.FoodResponse;
import com.vivafit.vivafit.manage_calories.services.FoodService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Food", description = "Food Controller")
@RequestMapping("/api/food")
@RestController
@Validated
public class FoodController {

    @Autowired
    private FoodService foodService;

    @PostMapping("/create")
    public ResponseEntity<FoodResponse> createFood(@RequestBody FoodDto foodDto) {
        Food createdFood = foodService.createFood(foodDto);
        FoodResponse response = new FoodResponse("Food created successfully", foodDto);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/getFoodByName/{name}")
    public ResponseEntity<FoodDto> getFoodByName(@PathVariable String name) {
        Food food = foodService.getFoodByName(name);
        if (food == null) {
            return ResponseEntity.notFound().build();
        }
        FoodDto foodDto = new FoodDto(food.getBarcode(), food.getName(), food.getCaloriesPer100g(),
                food.getProteinPer100g(), food.getFatPer100g(), food.getCarbsPer100g(), food.getCreatedBy());
        return ResponseEntity.ok(foodDto);
    }

    @GetMapping("/getFoodsByName/{name}")
    public ResponseEntity<List<FoodDto>> getFoodsByName(@PathVariable String name) {
        List<Food> foods = foodService.getFoodsByName(name);
        List<FoodDto> foodDtos = foods.stream().map(food -> new FoodDto(food.getBarcode(), food.getName(),
                        food.getCaloriesPer100g(), food.getProteinPer100g(), food.getFatPer100g(), food.getCarbsPer100g(), food.getCreatedBy()))
                .toList();
        return ResponseEntity.ok(foodDtos);
    }

    @PutMapping("/update/{name}")
    public ResponseEntity<FoodResponse> updateFood(@PathVariable String name, @RequestBody FoodDto foodDto) {
        Food updatedFood = foodService.updateFood(name, foodDto);
        if (updatedFood == null) {
            return ResponseEntity.notFound().build();
        }
        FoodResponse response = new FoodResponse("Food updated successfully", foodDto);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete/{name}")
    public ResponseEntity<FoodResponse> deleteFood(@PathVariable String name) {
        foodService.deleteFoodByName(name);
        return ResponseEntity.ok(new FoodResponse("Food deleted successfully", null));
    }
}
package com.vivafit.vivafit.manage_calories.controllers;

import com.vivafit.vivafit.authentification.entities.User;
import com.vivafit.vivafit.authentification.repositories.UserRepository;
import com.vivafit.vivafit.manage_calories.dto.AddFoodInMealRequestDto;
import com.vivafit.vivafit.manage_calories.dto.MealAnalizeAiDto;
import com.vivafit.vivafit.manage_calories.responses.MealFoodResponse;
import com.vivafit.vivafit.manage_calories.services.MealService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Tag(name = "Meal", description = "Meal Controller")
@RequestMapping("/api/meal")
@RestController
@Validated
public class MealController {
    @Autowired
    private MealService mealService;
    @Autowired
    private UserRepository userRepository;

    @PostMapping("/add-food-in-meal")
    public ResponseEntity<MealFoodResponse> addFoodToMeal(@RequestBody @Valid AddFoodInMealRequestDto request) {
        User user =  userRepository.findById(8).orElseThrow(() -> new RuntimeException("User not found"));
        MealFoodResponse response = mealService.addFoodInMeal(request, user);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/meal/analysis")
    public ResponseEntity<MealAnalizeAiDto> analyzeMeal(@RequestParam String mealType, @RequestParam LocalDate date) {
        User user =  userRepository.findById(8).orElseThrow(() -> new RuntimeException("User not found"));
        MealAnalizeAiDto response = mealService.getMealAnalysisForAI(date, mealType, 8);
        return ResponseEntity.ok(response);
    }
}

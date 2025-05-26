package com.vivafit.vivafit.manage_calories.controllers;

import com.vivafit.vivafit.authentification.entities.User;
import com.vivafit.vivafit.authentification.repositories.UserRepository;
import com.vivafit.vivafit.authentification.services.JwtService;
import com.vivafit.vivafit.manage_calories.dto.AddFoodInMealRequestDto;
import com.vivafit.vivafit.manage_calories.dto.RemoveFoodFromMealRequestDto;
import com.vivafit.vivafit.manage_calories.dto.UpdateFoodQuantityFromMealRequestDto;
import com.vivafit.vivafit.manage_calories.responses.MealDetailsResponse;
import com.vivafit.vivafit.manage_calories.responses.MealFoodResponse;
import com.vivafit.vivafit.manage_calories.services.MealService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "Meal", description = "Meal Controller")
@RequestMapping("/api/meal")
@RestController
@Validated
public class MealController {
    @Autowired
    private MealService mealService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtService jwtService;

    @PostMapping("/add-food-in-meal")
    public ResponseEntity<MealFoodResponse> addFoodToMeal(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader, @RequestBody @Valid AddFoodInMealRequestDto request) {
        User currentUser = jwtService.validateAndGetCurrentUser(authorizationHeader);
        MealFoodResponse response = mealService.addFoodInMeal(request, currentUser);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/remove-food-from-meal")
    public ResponseEntity<Void> removeFoodFromMeal(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                                                   @Valid @RequestBody RemoveFoodFromMealRequestDto request) {
        User currentUser = jwtService.validateAndGetCurrentUser(authorizationHeader);
        mealService.removeFoodFromMeal(request, currentUser);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/update-food-quantity-from-meal")
    public ResponseEntity<MealFoodResponse> updateFoodQuantityFromMeal(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                                                                       @Valid @RequestBody UpdateFoodQuantityFromMealRequestDto request) {
        User currentUser = jwtService.validateAndGetCurrentUser(authorizationHeader);
        MealFoodResponse response = mealService.updateFoodQuantityFromMeal(request, currentUser);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/get-details-of-meals/{date}")
    public ResponseEntity<List<MealDetailsResponse>> getDetailsOfMeals(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                                                                       @PathVariable("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        User currentUser = jwtService.validateAndGetCurrentUser(authorizationHeader);
        List<MealDetailsResponse> mealDetails = mealService.getDetailsOfMeals(date, currentUser);
        return ResponseEntity.ok(mealDetails);
    }

}

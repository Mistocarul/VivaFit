package com.vivafit.vivafit.manage_calories.controllers;

import com.vivafit.vivafit.authentification.entities.User;
import com.vivafit.vivafit.authentification.services.JwtService;
import com.vivafit.vivafit.manage_calories.dto.MealTypeUpdateRequestDto;
import com.vivafit.vivafit.manage_calories.responses.MealTypeResponse;
import com.vivafit.vivafit.manage_calories.services.MealTypeService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Meal Type", description = "Meal Type Controller")
@RequestMapping("/api/meal-type")
@RestController
@Validated
public class MealTypeController {
    @Autowired
    private MealTypeService mealTypeService;
    @Autowired
    private JwtService jwtService;

    @GetMapping("/find-meal-type-by-user")
    public MealTypeResponse getMealTypesByUser(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        User currentUser = jwtService.validateAndGetCurrentUser(authorizationHeader);
        return mealTypeService.getMealTypesByUser(currentUser);
    }

    @PutMapping("/update-meal-type-by-user")
    public MealTypeResponse updateMealTypesByUser(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                                                  @Valid @RequestBody MealTypeUpdateRequestDto requestDto){
        User currentUser = jwtService.validateAndGetCurrentUser(authorizationHeader);
        return mealTypeService.updateMealTypes(currentUser, requestDto);
    }
}

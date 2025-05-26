package com.vivafit.vivafit.manage_calories.controllers;

import com.vivafit.vivafit.authentification.entities.User;
import com.vivafit.vivafit.authentification.services.JwtService;
import com.vivafit.vivafit.manage_calories.dto.FoodDto;
import com.vivafit.vivafit.manage_calories.entities.Food;
import com.vivafit.vivafit.manage_calories.responses.FoodResponse;
import com.vivafit.vivafit.manage_calories.services.FoodService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
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
    @Autowired
    private JwtService jwtService;

    @PostMapping("/create")
    public ResponseEntity<FoodResponse> createFood(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader, @Valid @RequestBody FoodDto foodDto) {
        User currentUser = jwtService.validateAndGetCurrentUser(authorizationHeader);
        Food createdFood = foodService.createFood(foodDto);
        foodDto.setId(createdFood.getId());
        FoodResponse response = new FoodResponse("Food created successfully", foodDto);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/getFoodsByName/{name}")
    public ResponseEntity<List<FoodDto>> getFoodsByName(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader, @PathVariable String name) {
        User currentUser = jwtService.validateAndGetCurrentUser(authorizationHeader);
        List<Food> foods = foodService.getFoodsByName(name);
        List<FoodDto> foodDtos = foods.stream().map(food -> new FoodDto(food.getId() ,food.getBarcode(), food.getName(),
                        food.getCaloriesPer100g(), food.getProteinPer100g(), food.getFatPer100g(), food.getCarbsPer100g(), food.getCreatedBy()))
                .toList();
        return ResponseEntity.ok(foodDtos);
    }

    @GetMapping("/getFoodsByBarcode/{barcode}")
    public ResponseEntity<List<FoodDto>> getFoodsByBarcode(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader, @PathVariable String barcode) {
        User currentUser = jwtService.validateAndGetCurrentUser(authorizationHeader);
        List<Food> foods = foodService.getFoodsByBarcode(barcode);
        List<FoodDto> foodDtos = foods.stream().map(food -> new FoodDto(food.getId() ,food.getBarcode(), food.getName(),
                        food.getCaloriesPer100g(), food.getProteinPer100g(), food.getFatPer100g(), food.getCarbsPer100g(), food.getCreatedBy()))
                .toList();
        return ResponseEntity.ok(foodDtos);
    }


    @PutMapping("/update")
    public ResponseEntity<FoodResponse> updateFood(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,@Valid @RequestBody FoodDto foodDto) {
        User currentUser = jwtService.validateAndGetCurrentUser(authorizationHeader);
        Food updatedFood = foodService.updateFood(foodDto);
        if (updatedFood == null) {
            return ResponseEntity.notFound().build();
        }
        FoodResponse response = new FoodResponse("Food updated successfully", foodDto);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<FoodResponse> deleteFood(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader, @PathVariable Integer id) {
        User currentUser = jwtService.validateAndGetCurrentUser(authorizationHeader);
        foodService.deleteFoodById(id);
        return ResponseEntity.ok(new FoodResponse("Food deleted successfully", null));
    }
}
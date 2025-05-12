package com.vivafit.vivafit.manage_calories.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
public class MealFoodDetailsDto {

    private String foodName; // Numele alimentului
    private Double quantity; // Cantitatea consumată
    private Double calories; // Calorii pentru cantitatea respectivă
    private Double protein; // Proteine
    private Double fat; // Grăsimi
    private Double carbs; // Carbohidrați
}
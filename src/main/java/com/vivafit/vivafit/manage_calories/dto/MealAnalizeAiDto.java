package com.vivafit.vivafit.manage_calories.dto;


import com.vivafit.vivafit.manage_calories.entities.MealFood;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
public class MealAnalizeAiDto {
    private String mealType;
    private List<MealFoodDetailsDto> mealFoodDetails;
}

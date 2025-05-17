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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Tipul mesei: ").append(mealType).append("\n");
        sb.append("Detalii despre alimente:\n");
        for (MealFoodDetailsDto foodDetails : mealFoodDetails) {
            sb.append(" - ").append(foodDetails.getFoodName()).append(": ")
                    .append(foodDetails.getQuantity()).append("g, ")
                    .append(foodDetails.getCalories()).append(" kcal\n")
                    .append("Proteine: ").append(foodDetails.getProtein()).append("g, ")
                    .append("Grăsimi: ").append(foodDetails.getFat()).append("g, ")
                    .append("Carbohidrați: ").append(foodDetails.getCarbs()).append("g\n");
        }
        return sb.toString();
    }
}

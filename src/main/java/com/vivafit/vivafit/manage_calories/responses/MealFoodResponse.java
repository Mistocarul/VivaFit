package com.vivafit.vivafit.manage_calories.responses;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MealFoodResponse {
    private Integer id;
    private Integer foodId;
    private Integer mealId;
    private String foodName;
    private Double quantity;
    private Double calories;
    private Double protein;
    private Double fat;
    private Double carbs;
}
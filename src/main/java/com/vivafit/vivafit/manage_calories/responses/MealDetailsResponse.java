package com.vivafit.vivafit.manage_calories.responses;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MealDetailsResponse {
    private String mealType;
    List<MealFoodResponse> mealFoods;
}

package com.vivafit.vivafit.manage_calories.responses;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RecipeResponse {
    private Integer id;
    private String name;
    private String imagePath;
    private int portions;
    private int prepTimeMinutes;
    private int cookTimeMinutes;
    private int weightAfterCooking;
    private String preparationInstructions;
    private String ingredients;
    private double caloriesPer100g;
    private double proteinPer100g;
    private double fatPer100g;
    private double carbsPer100g;
    private String createdBy;
    private String userName;
}

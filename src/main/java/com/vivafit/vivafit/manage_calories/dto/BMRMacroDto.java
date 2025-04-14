package com.vivafit.vivafit.manage_calories.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
public class BMRMacroDto {
    @NotNull(message = "Calorie difference is required")
    @Min(value = -9999, message = "Calorie difference must be at least -9999")
    @Max(value = 9999, message = "Calorie difference must be at most 9999")
    private Integer calorieDifference;

    @NotNull(message = "Grams of carbs is required")
    @Min(value = 0, message = "Grams of carbs must be at least 0")
    @Max(value = 9999, message = "Grams of carbs must be at most 9999")
    private Integer gramsOfCarbs;

    @NotNull(message = "Grams of proteins is required")
    @Min(value = 0, message = "Grams of proteins must be at least 0")
    @Max(value = 9999, message = "Grams of proteins must be at most 9999")
    private Integer gramsOfProteins;

    @NotNull(message = "Grams of fats is required")
    @Min(value = 0, message = "Grams of fats must be at least 0")
    @Max(value = 9999, message = "Grams of fats must be at most 9999")
    private Integer gramsOfFats;
}
package com.vivafit.vivafit.manage_calories.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
public class CreateRecipeRequestDto {
    @NotNull(message = "Image is required")
    private MultipartFile image;

    @NotNull(message = "Name is required")
    @Size(max = 50, message = "Name must be at most 50 characters")
    private String name;

    @Min(value = 1, message = "Portions must be at least 1")
    @Max(value = 100, message = "Portions must be at most 100")
    private int portions;

    @Min(value = 0, message = "Preparation time must be at least 0")
    @Max(value = 1440, message = "Preparation time must be at most 1440 minutes")
    private int prepTimeMinutes;

    @Min(value = 0, message = "Cooking time must be at least 0")
    @Max(value = 1440, message = "Cooking time must be at most 1440 minutes")
    private int cookTimeMinutes;

    @Min(value = 0, message = "Weight after cooking must be at least 0")
    @Max(value = 10000, message = "Weight after cooking must be at most 10000 grams")
    private int weightAfterCooking;

    @NotNull(message = "Preparation instructions are required")
    @Size(max = 255, message = "Preparation instructions must be at most 255 characters")
    private String preparationInstructions;

    @NotNull(message = "Ingredients are required")
    @Size(max = 255, message = "Ingredients must be at most 255 characters")
    private String ingredients;

    @NotNull(message = "Calories per 100g is required")
    @DecimalMin(value = "0.0", message = "Calories per 100g must be at least 0")
    @DecimalMax(value = "1000.0", message = "Calories per 100g must be at most 1000")
    private double caloriesPer100g;

    @NotNull(message = "Protein per 100g is required")
    @DecimalMin(value = "0.0", message = "Protein per 100g must be at least 0")
    @DecimalMax(value = "100.0", message = "Protein per 100g must be at most 100")
    private double proteinPer100g;

    @NotNull(message = "Fat per 100g is required")
    @DecimalMin(value = "0.0", message = "Fat per 100g must be at least 0")
    @DecimalMax(value = "100.0", message = "Fat per 100g must be at most 100")
    private double fatPer100g;

    @NotNull(message = "Carbs per 100g is required")
    @DecimalMin(value = "0.0", message = "Carbs per 100g must be at least 0")
    @DecimalMax(value = "100.0", message = "Carbs per 100g must be at most 100")
    private double carbsPer100g;

    @NotNull(message = "Created by is required")
    @Size(max = 50, message = "Created by must be at most 50 characters")
    @Pattern(regexp = "ADMIN|USER", message = "Created by must be either ADMIN or USER")
    private String createdBy;
}
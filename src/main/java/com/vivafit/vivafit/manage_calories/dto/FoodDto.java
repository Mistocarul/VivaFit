package com.vivafit.vivafit.manage_calories.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;
import javax.validation.constraints.*;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
public class FoodDto {
    private Integer id;

    @NotBlank(message = "Barcode cannot be blank")
    private String barcode;

    @NotBlank(message = "Name cannot be blank")
    @Size(max = 100, message = "Name must not exceed 100 characters")
    private String name;

    @NotNull(message = "Calories per 100g cannot be null")
    @PositiveOrZero(message = "Calories per 100g must be zero or positive")
    private Double caloriesPer100g;

    @NotNull(message = "Protein per 100g cannot be null")
    @PositiveOrZero(message = "Protein per 100g must be zero or positive")
    private Double proteinPer100g;

    @NotNull(message = "Fat per 100g cannot be null")
    @PositiveOrZero(message = "Fat per 100g must be zero or positive")
    private Double fatPer100g;

    @NotNull(message = "Carbs per 100g cannot be null")
    @PositiveOrZero(message = "Carbs per 100g must be zero or positive")
    private Double carbsPer100g;

    @Pattern(regexp = "USER|ADMIN", message = "Created by must be either 'USER' or 'ADMIN'")
    @NotBlank(message = "Created by cannot be blank")
    private String createdBy;
}
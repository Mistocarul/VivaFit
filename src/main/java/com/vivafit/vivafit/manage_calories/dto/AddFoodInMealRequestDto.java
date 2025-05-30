package com.vivafit.vivafit.manage_calories.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
public class AddFoodInMealRequestDto {
    @NotNull(message = "Food ID cannot be null")
    private Integer foodId;

    @Positive(message = "Quantity must be positive")
    private Double quantity;

    @NotEmpty(message = "Meal type cannot be empty")
    private String mealType;

    @NotNull(message = "Date cannot be null")
    private LocalDate date;
}
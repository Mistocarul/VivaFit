package com.vivafit.vivafit.manage_calories.dto;

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
    private Integer foodId;

    @Positive(message = "Quantity must be positive")
    private Double quantity;

    private String mealType;

    private LocalDate date;

}

package com.vivafit.vivafit.manage_calories.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
public class RemoveFoodFromMealRequestDto {
    @NotNull(message = "Food ID cannot be null")
    private Integer foodId;

    @NotEmpty(message = "Meal type cannot be empty")
    private String mealType;

    @NotNull(message = "Date cannot be null")
    private LocalDate date;
}

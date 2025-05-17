package com.vivafit.vivafit.manage_calories.dto;

import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
public class MealTypeUpdateRequestDto {
    @Size(max = 30, message = "Meal type name must be at most 30 characters long")
    private String mealType1;

    @Size(max = 30, message = "Meal type name must be at most 30 characters long")
    private String mealType2;

    @Size(max = 30, message = "Meal type name must be at most 30 characters long")
    private String mealType3;

    @Size(max = 30, message = "Meal type name must be at most 30 characters long")
    private String mealType4;

    @Size(max = 30, message = "Meal type name must be at most 30 characters long")
    private String mealType5;
}

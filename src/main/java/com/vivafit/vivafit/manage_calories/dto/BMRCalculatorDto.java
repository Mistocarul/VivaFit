package com.vivafit.vivafit.manage_calories.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
public class BMRCalculatorDto {
    @NotEmpty(message = "Gender is required")
    @Pattern(regexp = "Masculin|Feminin", message = "Gender must be either 'Masculin' or 'Feminin'")
    private String gender;

    @NotNull(message = "Age is required")
    @Min(value = 1, message = "Age must be at least 1")
    @Max(value = 120, message = "Age must be at most 120")
    private Integer age;

    @NotNull(message = "Height is required")
    @Min(value = 50, message = "Height must be at least 50 cm")
    @Max(value = 300, message = "Height must be at most 300 cm")
    private Double heightInCm;

    @NotNull(message = "Weight is required")
    @Min(value = 10, message = "Weight must be at least 10 kg")
    @Max(value = 500, message = "Weight must be at most 500 kg")
    private Double weightInKg;

    @NotEmpty(message = "Activity level is required")
    @Pattern(regexp = "Sedentar|Putin activ|Moderat activ|Foarte activ|Extrem de activ", message = "Activity level must be one of the following: 'Sedentar', 'Putin activ', 'Moderat activ', 'Foarte activ', 'Extrem de activ'")
    private String activityLevelPerWeek;

    @NotEmpty(message = "Objective is required")
    @Pattern(regexp = "Scadere|Mentinere|Crestere", message = "Objective must be one of the following: 'Scadere', 'Mentinere', 'Crestere'")
    private String objective;
}
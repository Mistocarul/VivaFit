package com.vivafit.vivafit.ai.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class PredictionRequestDto {
    @NotNull(message = "Gender is required")
    @Min(value = 1, message = "Gender must be 1 or 2")
    @Max(value = 2, message = "Gender must be 1 or 2")
    private Integer gender;

    @NotNull(message = "Age is required")
    @Positive(message = "Age must be a positive number")
    private Integer age;

    @NotNull(message = "Height in cm is required")
    @Min(value = 150, message = "Height must be at least 150 cm")
    private Double heightInCm;

    @NotNull(message = "Weight in kg is required")
    @Min(value = 30, message = "Weight must be at least 30 kg")
    private Double weightInKg;

    @NotNull(message = "Duration in minutes is required")
    @Positive(message = "Duration must be a positive number")
    private Double durationInMinutes;

    private Double heartRateInBpm;

    private Double bodyTemperatureInCelsius;

    @AssertTrue(message = "Heart rate must be at least 30 bpm if provided")
    private boolean isHeartRateValid() {
        return heartRateInBpm == null || heartRateInBpm >= 30;
    }

    @AssertTrue(message = "Body temperature must be between 35 °C and 42 °C if provided")
    private boolean isBodyTemperatureValid() {
        return bodyTemperatureInCelsius == null || (bodyTemperatureInCelsius >= 35 && bodyTemperatureInCelsius <= 42);
    }

    @NotNull(message = "Approximate heart rate flag is required")
    private Boolean aproximateHeartRateInBpm;

    @NotNull(message = "Approximate body temperature flag is required")
    private Boolean aproximateBodyTemperatureInCelsius;

    @NotNull(message = "Activity level is required")
    @Pattern(regexp = "Usoara|Moderata|Intensa", message = "Activity level must be Usoara, Moderata, or Intensa")
    private String activityLevel;
}
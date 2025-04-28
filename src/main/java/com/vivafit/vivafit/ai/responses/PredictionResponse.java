package com.vivafit.vivafit.ai.responses;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class PredictionResponse {
    private Double predictedCalories;
}

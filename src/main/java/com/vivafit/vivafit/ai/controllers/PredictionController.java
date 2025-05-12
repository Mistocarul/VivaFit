package com.vivafit.vivafit.ai.controllers;

import com.vivafit.vivafit.ai.dto.PredictionRequestDto;
import com.vivafit.vivafit.ai.responses.PredictionResponse;
import com.vivafit.vivafit.ai.service.PredictionService;
import com.vivafit.vivafit.authentification.entities.User;
import com.vivafit.vivafit.authentification.services.JwtService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("/api/predict")
public class PredictionController {
    @Autowired
    private PredictionService predictionService;
    @Autowired
    private JwtService jwtService;

    @PostMapping("/calories")
    public PredictionResponse predictCalories(@RequestHeader("Authorization") String authorizationHeader, @Valid @RequestBody PredictionRequestDto request) {
        User currentUser = jwtService.validateAndGetCurrentUser(authorizationHeader);

        Boolean aproximateHeartRateInBpm = request.getAproximateHeartRateInBpm();
        Boolean aproximateBodyTemperatureInCelsius = request.getAproximateBodyTemperatureInCelsius();
        String activityLevel = request.getActivityLevel();

        if (aproximateHeartRateInBpm == true){
            Double heartRateInBpm = (double) (220 - request.getAge());
            if ("Usoara".equals(activityLevel)) {
                heartRateInBpm = heartRateInBpm * 0.5;
            } else if ("Moderata".equals(activityLevel)) {
                heartRateInBpm = heartRateInBpm * 0.7;
            } else if ("Intensa".equals(activityLevel)) {
                heartRateInBpm = heartRateInBpm * 0.85;
            }
            request.setHeartRateInBpm(heartRateInBpm);
        }

        if (aproximateBodyTemperatureInCelsius == true){
            Double bodyTemperatureInCelsius = 37.0;
            Double durationInMinutes = request.getDurationInMinutes();
            if (durationInMinutes <= 30) {
                bodyTemperatureInCelsius = bodyTemperatureInCelsius + 0.2;
            } else if (durationInMinutes <= 60) {
                bodyTemperatureInCelsius = bodyTemperatureInCelsius + 0.4;
            } else {
                bodyTemperatureInCelsius = bodyTemperatureInCelsius + 0.6;
            }
            if ("Usoara".equals(activityLevel)) {
                bodyTemperatureInCelsius = bodyTemperatureInCelsius + 0.3;
            } else if ("Moderata".equals(activityLevel)) {
                bodyTemperatureInCelsius = bodyTemperatureInCelsius + 0.5;
            } else if ("Intensa".equals(activityLevel)) {
                bodyTemperatureInCelsius = bodyTemperatureInCelsius + 0.7;
            }
            request.setBodyTemperatureInCelsius(bodyTemperatureInCelsius);
        }
        return predictionService.predictCalories(request);
    }
}

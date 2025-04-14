package com.vivafit.vivafit.manage_calories.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BMRDetailsResponse {
    private String gender;
    private Integer age;
    private Double height;
    private Double weight;
    private String activityLevel;
    private Integer calorieDifference;
    private String objective;
    private Integer numberOfCalories;
    private Integer gramsOfProteins;
    private Integer gramsOfFats;
    private Integer gramsOfCarbs;
    private String message;
}

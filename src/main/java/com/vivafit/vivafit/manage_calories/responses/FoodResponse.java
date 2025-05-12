package com.vivafit.vivafit.manage_calories.responses;

import com.vivafit.vivafit.manage_calories.dto.FoodDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FoodResponse {
    private String message;
    private FoodDto foodDto;
}
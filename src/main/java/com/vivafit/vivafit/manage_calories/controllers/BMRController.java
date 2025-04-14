package com.vivafit.vivafit.manage_calories.controllers;

import com.vivafit.vivafit.authentification.entities.User;
import com.vivafit.vivafit.authentification.services.JwtService;
import com.vivafit.vivafit.manage_calories.dto.BMRCalculatorDto;
import com.vivafit.vivafit.manage_calories.dto.BMRMacroDto;
import com.vivafit.vivafit.manage_calories.responses.BMRDetailsResponse;
import com.vivafit.vivafit.manage_calories.responses.BMRSimpleMessageResponse;
import com.vivafit.vivafit.manage_calories.services.BMRDetailsService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "BMR", description = "BMR Controller")
@RequestMapping("/api/bmr")
@RestController
@Validated
public class BMRController {
    @Autowired
    private BMRDetailsService bmrDetailsService;
    @Autowired
    private JwtService jwtService;

    @GetMapping("/user-bmr-details")
    public BMRDetailsResponse getUserBMRDetails(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        User currentUser = jwtService.validateAndGetCurrentUser(authorizationHeader);
        BMRDetailsResponse bmrDetailsResponse = bmrDetailsService.getBMRDetails(currentUser);
        return bmrDetailsResponse;
    }

    @PutMapping("/bmr-calculation")
    public BMRDetailsResponse calculateBMR(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                                           @Valid @RequestBody BMRCalculatorDto bmrCalculatorDto) {
        User currentUser = jwtService.validateAndGetCurrentUser(authorizationHeader);
        BMRDetailsResponse bmrDetailsResponse = bmrDetailsService.calculateBMR(currentUser, bmrCalculatorDto);
        return bmrDetailsResponse;
    }

    @PutMapping("/bmr-macro")
    public BMRSimpleMessageResponse calculateBMRMacros(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                                                       @Valid @RequestBody BMRMacroDto bmrMacroDto) {
        User currentUser = jwtService.validateAndGetCurrentUser(authorizationHeader);
        BMRSimpleMessageResponse bmrSimpleMessageResponse = bmrDetailsService.calculateBMRMacros(currentUser, bmrMacroDto);
        return bmrSimpleMessageResponse;
    }
}

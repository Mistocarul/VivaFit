package com.vivafit.vivafit.exercises.controllers;

import com.vivafit.vivafit.authentification.entities.User;
import com.vivafit.vivafit.authentification.services.JwtService;
import com.vivafit.vivafit.exercises.dto.ExerciseDto;
import com.vivafit.vivafit.exercises.services.ExerciseService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Exercises", description = "Exercises Controller")
@RequestMapping("/api/exercises")
@RestController
@Validated
public class ExerciseController {
    @Autowired
    private ExerciseService exerciseService;
    @Autowired
    private JwtService jwtService;

    @GetMapping("/all-exercises")
    public List<ExerciseDto> getAllExercises(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        User currentUser = jwtService.validateAndGetCurrentUser(authorizationHeader);
        return exerciseService.getAllExercises();
    }

}

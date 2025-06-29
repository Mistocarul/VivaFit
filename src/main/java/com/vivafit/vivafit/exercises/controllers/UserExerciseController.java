package com.vivafit.vivafit.exercises.controllers;

import com.vivafit.vivafit.authentification.entities.User;
import com.vivafit.vivafit.authentification.services.JwtService;
import com.vivafit.vivafit.exercises.dto.ExerciseDto;
import com.vivafit.vivafit.exercises.services.UserExerciseService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "User-exercises", description = "User-exercises Controller")
@RequestMapping("/api/user-exercises")
@RestController
@Validated
public class UserExerciseController {
    @Autowired
    private UserExerciseService userExerciseService;
    @Autowired
    private JwtService jwtService;

    @GetMapping("/all-exercises-by-user/{date}")
    public List<ExerciseDto> getAllExercisesByUser(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                                                   @PathVariable("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        User currentUser = jwtService.validateAndGetCurrentUser(authorizationHeader);
        return userExerciseService.getAllExercisesByUser(currentUser.getId(), date);
    }

    @PostMapping("/add-exercise-to-user")
    public ExerciseDto addExerciseToUser(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                                    @RequestBody ExerciseDto exerciseDto) {
        User currentUser = jwtService.validateAndGetCurrentUser(authorizationHeader);
        exerciseDto.setUserId(currentUser.getId());
        return userExerciseService.addExerciseToUser(exerciseDto);
    }

    @PutMapping("/update-exercise-to-user")
    public ExerciseDto updateExerciseToUser(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                                             @RequestBody ExerciseDto exerciseDto) {
        User currentUser = jwtService.validateAndGetCurrentUser(authorizationHeader);
        exerciseDto.setUserId(currentUser.getId());
        return userExerciseService.updateExerciseToUser(exerciseDto);
    }

    @DeleteMapping("/delete-exercise-from-user/{userExerciseId}")
    public void deleteExerciseFromUser(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                                       @PathVariable("userExerciseId") Integer userExerciseId) {
        User currentUser = jwtService.validateAndGetCurrentUser(authorizationHeader);
        userExerciseService.deleteExerciseFromUser(currentUser.getId(), userExerciseId);
    }
}

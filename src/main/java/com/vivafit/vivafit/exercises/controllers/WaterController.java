package com.vivafit.vivafit.exercises.controllers;

import com.vivafit.vivafit.authentification.entities.User;
import com.vivafit.vivafit.authentification.services.JwtService;
import com.vivafit.vivafit.exercises.dto.WaterDto;
import com.vivafit.vivafit.exercises.services.WaterService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "Water", description = "Water Controller")
@RequestMapping("/api/water")
@RestController
@Validated
public class WaterController {
    @Autowired
    private WaterService waterService;
    @Autowired
    private JwtService jwtService;

    @GetMapping("/all-water/{date}")
    public List<WaterDto> getAllWaterByUser(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                                                          @PathVariable("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        User currentUser = jwtService.validateAndGetCurrentUser(authorizationHeader);
        return waterService.getAllWaterByUser(currentUser.getId(), date);
    }

    @PutMapping("/update-water")
    public WaterDto updateWater(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                                @RequestBody WaterDto waterDto) {
        User currentUser = jwtService.validateAndGetCurrentUser(authorizationHeader);
        waterDto.setUserId(currentUser.getId());
        return waterService.updateWater(waterDto);
    }
}

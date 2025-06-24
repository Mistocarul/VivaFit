package com.vivafit.vivafit.specialist.controllers;

import com.vivafit.vivafit.authentification.entities.User;
import com.vivafit.vivafit.authentification.services.JwtService;
import com.vivafit.vivafit.specialist.dto.SpecialistDto;
import com.vivafit.vivafit.specialist.services.SpecialistService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Tag(name = "Specialist", description = "Specialist Controller")
@RequestMapping("/api/specialist")
@RestController
@Validated
public class SpecialistController {
    @Autowired
    private SpecialistService specialistService;
    @Autowired
    private JwtService jwtService;

    @PutMapping("/update-profile")
    public SpecialistDto updateProfile(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader, @Valid @ModelAttribute SpecialistDto specialistDto) throws IOException {
        User currentUser = jwtService.validateAndGetCurrentUser(authorizationHeader);
        specialistDto.setUserId(currentUser.getId());
        return specialistService.updateProfile(specialistDto);
    }

    @GetMapping("/get-profile")
    public SpecialistDto getProfile(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        User currentUser = jwtService.validateAndGetCurrentUser(authorizationHeader);
        return specialistService.getProfile(currentUser.getId());
    }

    @PutMapping("/add-visit-profile")
    public SpecialistDto addVisitProfile(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader, @Valid @ModelAttribute SpecialistDto specialistDto) throws IOException {
        User currentUser = jwtService.validateAndGetCurrentUser(authorizationHeader);
        return specialistService.addVisitProfile(specialistDto);
    }

    @GetMapping("/get-specialists-by-name")
    public ResponseEntity<Page<SpecialistDto>> getSpecialistsByName(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
            @RequestParam String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        User currentUser = jwtService.validateAndGetCurrentUser(authorizationHeader);
        Page<SpecialistDto> specialists = specialistService.getSpecialistsByName(name, PageRequest.of(page, size));
        return ResponseEntity.ok(specialists);
    }

    @GetMapping("/get-specialists-all")
    public ResponseEntity<Page<SpecialistDto>> getSpecialists(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        User currentUser = jwtService.validateAndGetCurrentUser(authorizationHeader);
        Page<SpecialistDto> specialists = specialistService.getSpecialistsAll(PageRequest.of(page, size));
        return ResponseEntity.ok(specialists);
    }

    @GetMapping("/get-specialists-nutritionists")
    public ResponseEntity<Page<SpecialistDto>> getNutritionists(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        User currentUser = jwtService.validateAndGetCurrentUser(authorizationHeader);
        Page<SpecialistDto> nutritionists = specialistService.getSpecialistsNutritionists(PageRequest.of(page, size));
        return ResponseEntity.ok(nutritionists);
    }

    @GetMapping("/get-specialists-coaches")
    public ResponseEntity<Page<SpecialistDto>> getCoaches(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        User currentUser = jwtService.validateAndGetCurrentUser(authorizationHeader);
        Page<SpecialistDto> coaches = specialistService.getSpecialistsCoaches(PageRequest.of(page, size));
        return ResponseEntity.ok(coaches);
    }
}

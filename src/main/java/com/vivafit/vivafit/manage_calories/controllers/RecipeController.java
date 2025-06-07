package com.vivafit.vivafit.manage_calories.controllers;

import com.vivafit.vivafit.authentification.entities.User;
import com.vivafit.vivafit.authentification.services.JwtService;
import com.vivafit.vivafit.manage_calories.dto.CreateRecipeRequestDto;
import com.vivafit.vivafit.manage_calories.responses.RecipeResponse;
import com.vivafit.vivafit.manage_calories.services.RecipeService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Recipe", description = "Recipe Controller")
@RequestMapping("/api/recipes")
@RestController
@Validated
public class RecipeController {
    @Autowired
    private RecipeService recipeService;
    @Autowired
    private JwtService jwtService;

    @PostMapping("/create")
    public ResponseEntity<RecipeResponse> createRecipe(@Valid @ModelAttribute CreateRecipeRequestDto request, @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        User currentUser = jwtService.validateAndGetCurrentUser(authorizationHeader);
        RecipeResponse response = recipeService.createRecipe(request, currentUser.getId());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRecipe(@PathVariable Integer id, @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        User currentUser = jwtService.validateAndGetCurrentUser(authorizationHeader);
        recipeService.deleteRecipe(id, currentUser.getId());
        return ResponseEntity.ok("Recipe deleted successfully");
    }

    @GetMapping("/search-by-name")
    public ResponseEntity<List<RecipeResponse>> searchRecipes(@RequestParam String name, @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        User currentUser = jwtService.validateAndGetCurrentUser(authorizationHeader);
        return ResponseEntity.ok(recipeService.searchRecipesByName(name));
    }

    @GetMapping("/get-by-user")
    public ResponseEntity<List<RecipeResponse>> getRecipesByUser(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        User currentUser = jwtService.validateAndGetCurrentUser(authorizationHeader);
        List<RecipeResponse> recipes = recipeService.getRecipesByUser(currentUser.getId());
        return ResponseEntity.ok(recipes);
    }

    @GetMapping("/get-my-favorites")
    public ResponseEntity<List<RecipeResponse>> getMyFavoriteRecipes(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        User currentUser = jwtService.validateAndGetCurrentUser(authorizationHeader);
        List<RecipeResponse> favoriteRecipes = recipeService.getMyFavoriteRecipes(currentUser.getId());
        return ResponseEntity.ok(favoriteRecipes);
    }
}

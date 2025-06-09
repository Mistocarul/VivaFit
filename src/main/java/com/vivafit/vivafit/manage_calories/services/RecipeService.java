package com.vivafit.vivafit.manage_calories.services;

import com.vivafit.vivafit.authentification.repositories.UserRepository;
import com.vivafit.vivafit.manage_calories.dto.CreateRecipeRequestDto;
import com.vivafit.vivafit.manage_calories.entities.Food;
import com.vivafit.vivafit.manage_calories.entities.Recipe;
import com.vivafit.vivafit.manage_calories.repositories.FoodFavoriteRepository;
import com.vivafit.vivafit.manage_calories.repositories.FoodRepository;
import com.vivafit.vivafit.manage_calories.repositories.RecipeRepository;
import com.vivafit.vivafit.manage_calories.responses.FoodResponse;
import com.vivafit.vivafit.manage_calories.responses.RecipeResponse;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Getter
@Setter
@Service
public class RecipeService {
    @Autowired
    private RecipeRepository recipeRepository;
    @Autowired
    private FoodRepository foodRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private FoodFavoriteRepository foodFavoriteRepository;

    @Value("${upload.folder.recipes.path}")
    private String uploadFolderRecipesPath;

    public RecipeResponse createRecipe(CreateRecipeRequestDto request, Integer userId) {
        if (recipeRepository.findByNameAndUserId(request.getName(), userId).isPresent()) {
            throw new RuntimeException("Recipe already exists for this user.");
        }
        String fileName = request.getName() + "_" + userId + ".png";
        String imagePath = uploadFolderRecipesPath + fileName;
        Path filePath = Path.of(imagePath);
        try {
            request.getImage().transferTo(filePath);
        } catch (Exception e) {
            throw new RuntimeException("Failed to save image: " + e.getMessage());
        }
        Food food = Food.builder()
                .name(request.getName())
                .barcode("reteta")
                .caloriesPer100g(request.getCaloriesPer100g())
                .proteinPer100g(request.getProteinPer100g())
                .fatPer100g(request.getFatPer100g())
                .carbsPer100g(request.getCarbsPer100g())
                .createdBy(request.getCreatedBy())
                .build();
        food = foodRepository.save(food);

        Recipe recipe = Recipe.builder()
                .id(food.getId())
                .name(request.getName())
                .portions(request.getPortions())
                .prepTimeMinutes(request.getPrepTimeMinutes())
                .cookTimeMinutes(request.getCookTimeMinutes())
                .weightAfterCooking(request.getWeightAfterCooking())
                .preparationInstructions(request.getPreparationInstructions())
                .ingredients(request.getIngredients())
                .caloriesPer100g(request.getCaloriesPer100g())
                .proteinPer100g(request.getProteinPer100g())
                .fatPer100g(request.getFatPer100g())
                .carbsPer100g(request.getCarbsPer100g())
                .imagePath(imagePath)
                .createdBy(request.getCreatedBy())
                .userId(userId)
                .build();
        recipe = recipeRepository.save(recipe);
        return mapToResponse(recipe);

    }

    public void deleteRecipe(Integer recipeId, Integer userId) {
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new RuntimeException("Recipe not found"));
        Food food = foodRepository.findById(recipeId)
                .orElseThrow(() -> new RuntimeException("Food not found"));

        if (!recipe.getCreatedBy().equals(userId)) {
            throw new RuntimeException("Unauthorized delete attempt");
        }

        Path filePath = Paths.get(recipe.getImagePath());
        try {
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete image file");
        }

        recipeRepository.delete(recipe);
        foodRepository.delete(food);
    }

    public List<RecipeResponse> searchRecipesByName(String name) {
        return recipeRepository.findByNameContainingIgnoreCase(name)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public List<RecipeResponse> getRecipesByUser(Integer userId) {
        return recipeRepository.findByUserId(userId)
                .orElseGet(List::of)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public List<RecipeResponse> getMyFavoriteRecipes(Integer userId) {
        return foodFavoriteRepository.findByUserId(userId)
                .stream()
                .map(favorite -> recipeRepository.findById(favorite.getFoodId()).orElse(null))
                .filter(recipe -> recipe != null)
                .map(this::mapToResponse)
                .toList();
    }

    private RecipeResponse mapToResponse(Recipe recipe) {
        return RecipeResponse.builder()
                .id(recipe.getId())
                .name(recipe.getName())
                .imagePath(recipe.getImagePath())
                .portions(recipe.getPortions())
                .prepTimeMinutes(recipe.getPrepTimeMinutes())
                .cookTimeMinutes(recipe.getCookTimeMinutes())
                .weightAfterCooking(recipe.getWeightAfterCooking())
                .preparationInstructions(recipe.getPreparationInstructions())
                .ingredients(recipe.getIngredients())
                .caloriesPer100g(recipe.getCaloriesPer100g())
                .proteinPer100g(recipe.getProteinPer100g())
                .fatPer100g(recipe.getFatPer100g())
                .carbsPer100g(recipe.getCarbsPer100g())
                .createdBy(recipe.getCreatedBy())
                .userName(userRepository.findById(recipe.getUserId())
                        .orElseThrow(() -> new RuntimeException("User not found"))
                        .getUsername())
                .build();
    }

    public List<RecipeResponse> getGeneralRecipes(Integer id) {
        List<Recipe> recipes = recipeRepository.findByIdGreaterThanOrderByIdAsc(id)
                .stream()
                .limit(30)
                .toList();

        return recipes.stream()
                .map(this::mapToResponse)
                .toList();
    }
}

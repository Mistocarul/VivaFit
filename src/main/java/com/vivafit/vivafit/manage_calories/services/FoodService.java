package com.vivafit.vivafit.manage_calories.services;

import com.vivafit.vivafit.authentification.entities.User;
import com.vivafit.vivafit.authentification.repositories.UserRepository;
import com.vivafit.vivafit.manage_calories.dto.FoodDto;
import com.vivafit.vivafit.manage_calories.entities.Food;
import com.vivafit.vivafit.manage_calories.entities.FoodFavorite;
import com.vivafit.vivafit.manage_calories.entities.Recipe;
import com.vivafit.vivafit.manage_calories.repositories.FoodFavoriteRepository;
import com.vivafit.vivafit.manage_calories.repositories.FoodRepository;
import com.vivafit.vivafit.manage_calories.repositories.RecipeRepository;
import com.vivafit.vivafit.manage_calories.responses.FoodResponse;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Getter
@Setter
@Service
public class FoodService {

    @Autowired
    private FoodRepository foodRepository;
    @Autowired
    private FoodFavoriteRepository foodFavoriteRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RecipeRepository recipeRepository;

    public Food createFood(FoodDto foodDto, Integer userId) {
        Food food = new Food();
        food.setBarcode(foodDto.getBarcode());
        food.setName(foodDto.getName());
        food.setCaloriesPer100g(foodDto.getCaloriesPer100g());
        food.setProteinPer100g(foodDto.getProteinPer100g());
        food.setFatPer100g(foodDto.getFatPer100g());
        food.setCarbsPer100g(foodDto.getCarbsPer100g());
        food.setCreatedBy(foodDto.getCreatedBy());
        food.setUserId(userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        String userRole = user.getRole();
        if ("COACH".equals(userRole) || "NUTRITIONIST".equals(userRole) || "USER".equals(userRole)) {
            food.setCreatedBy("USER");
        } else if ("ADMIN".equals(userRole)) {
            food.setCreatedBy("ADMIN");
        }

        food.setCreatedBy(userRole);
        Food savedFood = foodRepository.save(food);
        return savedFood;
    }

    public List<Food> getFoodsByName(String name) {
        return foodRepository.findByNameContainingIgnoreCase(name);
    }

    public List<Food> getFoodsByBarcode(String barcode) {
        return foodRepository.findByBarcodeContainingIgnoreCase(barcode);
    }

    public void deleteFoodById(Integer id) {
        Food food = foodRepository.findById(id).orElse(null);
        if (food != null) {
            foodRepository.delete(food);
        }
    }

    public Food getFoodById(Integer id) {
        return foodRepository.findById(id).orElse(null);
    }

    public Food updateFood(FoodDto foodDto, Integer userId) {
        Food food = foodRepository.findById(foodDto.getId()).orElse(null);
        if (food != null) {
            food.setBarcode(foodDto.getBarcode());
            food.setName(foodDto.getName());
            food.setCaloriesPer100g(foodDto.getCaloriesPer100g());
            food.setProteinPer100g(foodDto.getProteinPer100g());
            food.setFatPer100g(foodDto.getFatPer100g());
            food.setCarbsPer100g(foodDto.getCarbsPer100g());
            food.setCreatedBy(foodDto.getCreatedBy());
            food.setUserId(userId);
            return foodRepository.save(food);
        }
        return null;
    }

    public void addFoodToFavorites(Integer userId, Integer foodId) {
        FoodFavorite foodFavorite = new FoodFavorite();
        foodFavorite.setUserId(userId);
        foodFavorite.setFoodId(foodId);
        foodFavoriteRepository.save(foodFavorite);
    }

    public void removeFoodFromFavorites(Integer userId, Integer foodId) {
        FoodFavorite foodFavorite = foodFavoriteRepository.findByUserIdAndFoodId(userId, foodId);
        if (foodFavorite != null) {
            foodFavoriteRepository.delete(foodFavorite);
        }
    }

    public boolean isFoodFavorite(Integer userId, Integer foodId) {
        FoodFavorite foodFavorite = foodFavoriteRepository.findByUserIdAndFoodId(userId, foodId);
        if (foodFavorite != null) {
            return true;
        }
        return false;
    }

    public List<Food> getFavoriteFoods(Integer userId) {
        List<FoodFavorite> foodFavorites = foodFavoriteRepository.findByUserId(userId);
        return foodFavorites.stream()
                .map(foodFavorite -> foodRepository.findById(foodFavorite.getFoodId()).orElse(null))
                .filter(food -> food != null)
                .toList();
    }

    public void deleteRelationFoodUser(Integer foodId, Integer userId) {
        Food food = foodRepository.findById(foodId)
                .orElseThrow(() -> new RuntimeException("Food not found"));

        if (!food.getUserId().equals(userId)) {
            throw new RuntimeException("You do not have permission to delete this relation");
        }

        Integer adminUserId = userRepository.findFirstByRole("ADMIN")
                .orElseThrow(() -> new RuntimeException("No user with ADMIN role found"))
                .getId();

        food.setCreatedBy("ADMIN");
        food.setUserId(adminUserId);
        foodRepository.save(food);

        Optional<Recipe> optionalRecipe = recipeRepository.findById(foodId);

        if (optionalRecipe.isPresent()) {
            Recipe recipe = optionalRecipe.get();

            if (!recipe.getUserId().equals(userId)) {
                throw new RuntimeException("You do not have permission to delete this relation");
            }

            Integer adminUserId2 = userRepository.findFirstByRole("ADMIN")
                    .orElseThrow(() -> new RuntimeException("No user with ADMIN role found"))
                    .getId();

            recipe.setCreatedBy("ADMIN");
            recipe.setUserId(adminUserId2);
            recipeRepository.save(recipe);
        }
    }

    public List<Food> getFoodsByUserId(Integer userId) {
        return foodRepository.findByUserId(userId).orElseGet(List::of);
    }

    public FoodResponse updateFoodBarcode(Food food) {
        Food updatedFood = foodRepository.save(food);
        User user = userRepository.findById(updatedFood.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        String userUsername = user.getUsername();

        FoodDto foodDto = new FoodDto(
                updatedFood.getId(),
                updatedFood.getBarcode(),
                updatedFood.getName(),
                updatedFood.getCaloriesPer100g(),
                updatedFood.getProteinPer100g(),
                updatedFood.getFatPer100g(),
                updatedFood.getCarbsPer100g(),
                updatedFood.getCreatedBy(),
                userUsername
        );
        return new FoodResponse("Barcode updated successfully", foodDto);
    }
}

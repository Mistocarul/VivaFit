package com.vivafit.vivafit.manage_calories.controllers;

import com.vivafit.vivafit.authentification.entities.User;
import com.vivafit.vivafit.authentification.repositories.UserRepository;
import com.vivafit.vivafit.authentification.services.JwtService;
import com.vivafit.vivafit.manage_calories.dto.FoodDto;
import com.vivafit.vivafit.manage_calories.entities.Food;
import com.vivafit.vivafit.manage_calories.responses.FoodResponse;
import com.vivafit.vivafit.manage_calories.services.FoodService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriUtils;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Tag(name = "Food", description = "Food Controller")
@RequestMapping("/api/food")
@RestController
@Validated
public class FoodController {

    @Autowired
    private FoodService foodService;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private UserRepository userRepository;

    @PostMapping("/create")
    public ResponseEntity<FoodResponse> createFood(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader, @Valid @RequestBody FoodDto foodDto) {
        User currentUser = jwtService.validateAndGetCurrentUser(authorizationHeader);
        Food createdFood = foodService.createFood(foodDto, currentUser.getId());
        foodDto.setId(createdFood.getId());
        FoodResponse response = new FoodResponse("Food created successfully", foodDto);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/getFoodsByName/{name}")
    public ResponseEntity<List<FoodDto>> getFoodsByName(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader, @PathVariable String name) {
        User currentUser = jwtService.validateAndGetCurrentUser(authorizationHeader);
        List<Food> foods = foodService.getFoodsByName(name);

        if (foods.isEmpty() || foods == null) {
            String url = "https://world.openfoodfacts.org/cgi/search.pl?search_terms="
                    + UriUtils.encode(name, StandardCharsets.UTF_8) +
                    "&search_simple=1&action=process&json=1&page_size=5";
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            List<Map<String, Object>> products = (List<Map<String, Object>>) response.getBody().get("products");
            if (!products.isEmpty()) {
                Map<String, Object> product = products.get(0);
                Map<String, Object> nutriments = (Map<String, Object>) product.get("nutriments");

                String barcode = (String) product.getOrDefault("code", null);
                String productName = ((String) product.getOrDefault("product_name", "Unknown"));
                if (productName.length() > 50) {
                    productName = productName.substring(0, 50);
                }

                Double calories = nutriments.get("energy-kcal_100g") != null ?
                        Double.valueOf(nutriments.get("energy-kcal_100g").toString()) : 0.0;

                Double protein = nutriments.get("proteins_100g") != null ?
                        Double.valueOf(nutriments.get("proteins_100g").toString()) : 0.0;

                Double fat = nutriments.get("fat_100g") != null ?
                        Double.valueOf(nutriments.get("fat_100g").toString()) : 0.0;

                Double carbs = nutriments.get("carbohydrates_100g") != null ?
                        Double.valueOf(nutriments.get("carbohydrates_100g").toString()) : 0.0;

                Food newFood = Food.builder()
                        .barcode(barcode)
                        .name(productName)
                        .caloriesPer100g(calories)
                        .proteinPer100g(protein)
                        .fatPer100g(fat)
                        .carbsPer100g(carbs)
                        .createdBy("ADMIN")
                        .userId(13)
                        .build();

                newFood = foodService.saveFood(newFood);
                foods = List.of(newFood);
            }
        }

        List<FoodDto> foodDtos = foods.stream().map(food -> {
            String userName = userRepository.findById(food.getUserId())
                    .map(User::getUsername)
                    .orElse("Unknown User");
            return new FoodDto(food.getId(), food.getBarcode(), food.getName(),
                    food.getCaloriesPer100g(), food.getProteinPer100g(), food.getFatPer100g(),
                    food.getCarbsPer100g(), food.getCreatedBy(), userName);
        }).toList();

        return ResponseEntity.ok(foodDtos);
    }

    @GetMapping("/getFoodsById/{id}")
    public ResponseEntity<FoodDto> getFoodById(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader, @PathVariable Integer id) {
        User currentUser = jwtService.validateAndGetCurrentUser(authorizationHeader);
        Food food = foodService.getFoodById(id);
        if (food == null) {
            return ResponseEntity.notFound().build();
        }
        String userName = userRepository.findById(food.getUserId())
                .map(User::getUsername)
                .orElse("Unknown User");
        FoodDto foodDto = new FoodDto(food.getId(), food.getBarcode(), food.getName(),
                food.getCaloriesPer100g(), food.getProteinPer100g(), food.getFatPer100g(),
                food.getCarbsPer100g(), food.getCreatedBy(), userName);
        return ResponseEntity.ok(foodDto);
    }

    @GetMapping("/getFoodsByBarcode/{barcode}")
    public ResponseEntity<List<FoodDto>> getFoodsByBarcode(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
            @PathVariable String barcode) {

        User currentUser = jwtService.validateAndGetCurrentUser(authorizationHeader);
        List<Food> foods = Optional.ofNullable(foodService.getFoodsByBarcode(barcode)).orElseGet(List::of);

        if (foods.isEmpty()) {
            String url = "https://world.openfoodfacts.org/api/v0/product/" + barcode + ".json";

            try {
                RestTemplate restTemplate = new RestTemplate();
                ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);

                if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                    Map<String, Object> productData = (Map<String, Object>) response.getBody().get("product");

                    if (productData != null) {
                        String productName = (String) productData.getOrDefault("product_name", "Unknown");
                        if (productName.length() > 50) {
                            productName = productName.substring(0, 50);
                        }

                        Map<String, Object> nutriments = (Map<String, Object>) productData.getOrDefault("nutriments", Map.of());

                        double calories = ((Number) nutriments.getOrDefault("energy-kcal_100g", 0)).doubleValue();
                        double protein = ((Number) nutriments.getOrDefault("proteins_100g", 0)).doubleValue();
                        double fat = ((Number) nutriments.getOrDefault("fat_100g", 0)).doubleValue();
                        double carbs = ((Number) nutriments.getOrDefault("carbohydrates_100g", 0)).doubleValue();

                        Food newFood = Food.builder()
                                .barcode(barcode)
                                .name(productName)
                                .caloriesPer100g(calories)
                                .proteinPer100g(protein)
                                .fatPer100g(fat)
                                .carbsPer100g(carbs)
                                .createdBy("ADMIN")
                                .userId(currentUser.getId())
                                .build();

                        newFood = foodService.saveFood(newFood);

                        foods = List.of(newFood);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        List<FoodDto> foodDtos = foods.stream().map(food -> {
            String userName = userRepository.findById(food.getUserId())
                    .map(User::getUsername)
                    .orElse("Unknown User");
            return new FoodDto(food.getId(), food.getBarcode(), food.getName(),
                    food.getCaloriesPer100g(), food.getProteinPer100g(), food.getFatPer100g(),
                    food.getCarbsPer100g(), food.getCreatedBy(), userName);
        }).toList();

        return ResponseEntity.ok(foodDtos);
    }


    @PutMapping("/delete-relation-food-user/{id}")
    public ResponseEntity<?> deleteRelationFoodUser(@PathVariable Integer id, @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        User currentUser = jwtService.validateAndGetCurrentUser(authorizationHeader);
        foodService.deleteRelationFoodUser(id, currentUser.getId());
        return ResponseEntity.ok("Relation deleted successfully");
    }

    @PutMapping("/associate-barcode/{id}")
    public ResponseEntity<FoodResponse> associateBarcode(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader, @PathVariable Integer id, @RequestParam String barcode) {
        User currentUser = jwtService.validateAndGetCurrentUser(authorizationHeader);
        Food food = foodService.getFoodById(id);
        if (food == null) {
            return ResponseEntity.notFound().build();
        }
        food.setBarcode(barcode);
        FoodResponse response = foodService.updateFoodBarcode(food);
        return ResponseEntity.ok(response);
    }


    @PutMapping("/update")
    public ResponseEntity<FoodResponse> updateFood(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,@Valid @RequestBody FoodDto foodDto) {
        User currentUser = jwtService.validateAndGetCurrentUser(authorizationHeader);
        Food updatedFood = foodService.updateFood(foodDto, currentUser.getId());
        if (updatedFood == null) {
            return ResponseEntity.notFound().build();
        }
        FoodResponse response = new FoodResponse("Food updated successfully", foodDto);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<FoodResponse> deleteFood(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader, @PathVariable Integer id) {
        User currentUser = jwtService.validateAndGetCurrentUser(authorizationHeader);
        foodService.deleteFoodById(id);
        return ResponseEntity.ok(new FoodResponse("Food deleted successfully", null));
    }

    @GetMapping("/isFavorite/{foodId}")
    public ResponseEntity<Boolean> isFavorite(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader, @PathVariable Integer foodId) {
        User currentUser = jwtService.validateAndGetCurrentUser(authorizationHeader);
        boolean isFavorite = foodService.isFoodFavorite(currentUser.getId(), foodId);
        return ResponseEntity.ok(isFavorite);
    }

    @GetMapping("/isCreatedByCurrentUser/{foodId}")
    public ResponseEntity<Boolean> isCreatedByCurrentUser(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader, @PathVariable Integer foodId) {
        User currentUser = jwtService.validateAndGetCurrentUser(authorizationHeader);
        Food food = foodService.getFoodById(foodId);
        if (food == null) {
            return ResponseEntity.notFound().build();
        }
        boolean isCreatedByCurrentUser = food.getUserId().equals(currentUser.getId());
        return ResponseEntity.ok(isCreatedByCurrentUser);
    }

    @GetMapping("/getFavorites")
    public ResponseEntity<List<FoodDto>> getFavorites(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        User currentUser = jwtService.validateAndGetCurrentUser(authorizationHeader);
        List<Food> favoriteFoods = foodService.getFavoriteFoods(currentUser.getId());

        List<FoodDto> foodDtos = favoriteFoods.stream().map(food -> {
            String userName = userRepository.findById(food.getUserId())
                    .map(User::getUsername)
                    .orElse("Unknown User");
            return new FoodDto(food.getId(), food.getBarcode(), food.getName(),
                    food.getCaloriesPer100g(), food.getProteinPer100g(), food.getFatPer100g(),
                    food.getCarbsPer100g(), food.getCreatedBy(), userName);
        }).toList();

        return ResponseEntity.ok(foodDtos);
    }

    @GetMapping("/getMyFoods")
    public ResponseEntity<List<FoodDto>> getMyFoods(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        User currentUser = jwtService.validateAndGetCurrentUser(authorizationHeader);
        List<Food> myFoods = foodService.getFoodsByUserId(currentUser.getId());

        List<FoodDto> foodDtos = myFoods.stream().map(food -> {
            String userName = userRepository.findById(food.getUserId())
                    .map(User::getUsername)
                    .orElse("Unknown User");
            return new FoodDto(food.getId(), food.getBarcode(), food.getName(),
                    food.getCaloriesPer100g(), food.getProteinPer100g(), food.getFatPer100g(),
                    food.getCarbsPer100g(), food.getCreatedBy(), userName);
        }).toList();

        return ResponseEntity.ok(foodDtos);
    }

    @PostMapping("/addToFavorites/{foodId}")
    public ResponseEntity<FoodResponse> addToFavorites(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader, @PathVariable Integer foodId) {
        User currentUser = jwtService.validateAndGetCurrentUser(authorizationHeader);
        foodService.addFoodToFavorites(currentUser.getId(), foodId);
        return ResponseEntity.ok(new FoodResponse("Food added to favorites successfully", null));
    }

    @DeleteMapping("/removeFromFavorites/{foodId}")
    public ResponseEntity<FoodResponse> removeFromFavorites(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader, @PathVariable Integer foodId) {
        User currentUser = jwtService.validateAndGetCurrentUser(authorizationHeader);
        foodService.removeFoodFromFavorites(currentUser.getId(), foodId);
        return ResponseEntity.ok(new FoodResponse("Food removed from favorites successfully", null));
    }
}
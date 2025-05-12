package com.vivafit.vivafit.manage_calories.services;

import com.vivafit.vivafit.authentification.entities.User;
import com.vivafit.vivafit.authentification.repositories.UserRepository;
import com.vivafit.vivafit.manage_calories.dto.AddFoodInMealRequestDto;
import com.vivafit.vivafit.manage_calories.dto.MealAnalizeAiDto;
import com.vivafit.vivafit.manage_calories.dto.MealFoodDetailsDto;
import com.vivafit.vivafit.manage_calories.responses.MealFoodResponse;
import com.vivafit.vivafit.manage_calories.entities.Food;
import com.vivafit.vivafit.manage_calories.entities.Meal;
import com.vivafit.vivafit.manage_calories.entities.MealFood;
import com.vivafit.vivafit.manage_calories.repositories.FoodRepository;
import com.vivafit.vivafit.manage_calories.repositories.MealFoodRepository;
import com.vivafit.vivafit.manage_calories.repositories.MealRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class MealService {
    @Autowired
    private MealRepository mealRepository;
    @Autowired
    private MealFoodRepository mealFoodRepository;
    @Autowired
    private FoodRepository foodRepository;
    @Autowired
    private UserRepository userRepository;

    @Transactional
    public MealFoodResponse addFoodInMeal(AddFoodInMealRequestDto request, User user) {
        Meal meal = mealRepository.findByDateAndUserAndMealType(request.getDate(), user, request.getMealType())
                .orElseGet(() -> {
                    Meal newMeal = Meal.builder()
                            .date(request.getDate())
                            .mealType(request.getMealType())
                            .user(user)
                            .mealFoods(new ArrayList<>())
                            .build();
                    return mealRepository.save(newMeal);
                });

        Food food = foodRepository.findById(request.getFoodId())
                .orElseThrow(() -> new RuntimeException("Food not found"));

        double factor = request.getQuantity() / 100;

        MealFood mealFood = MealFood.builder()
                .meal(meal)
                .food(food)
                .quantity(request.getQuantity())
                .calories(food.getCaloriesPer100g() * factor)
                .protein(food.getProteinPer100g() * factor)
                .fat(food.getFatPer100g() * factor)
                .carbs(food.getCarbsPer100g() * factor)
                .build();

        MealFood savedMealFood = mealFoodRepository.save(mealFood);

        return MealFoodResponse.builder()
                .id(savedMealFood.getId())
                .foodName(food.getName())
                .quantity(savedMealFood.getQuantity())
                .calories(savedMealFood.getCalories())
                .protein(savedMealFood.getProtein())
                .fat(savedMealFood.getFat())
                .carbs(savedMealFood.getCarbs())
                .build();
    }

    @Transactional
    public MealAnalizeAiDto getMealAnalysisForAI(LocalDate mealDate, String mealType, Integer userId) {
        // Căutăm masa în funcție de user, tipul mesei și data mesei
        Optional<Meal> mealOptional = mealRepository.findByDateAndUserAndMealType(mealDate, userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found")), mealType);

        if (!mealOptional.isPresent()) {
            throw new RuntimeException("Meal not found for the given date, meal type, and user.");
        }

        Meal meal = mealOptional.get();

        // Construim lista de MealFoodDetailsDto pentru AI
        List<MealFoodDetailsDto> mealFoodDetails = new ArrayList<>();
        for (MealFood mealFood : meal.getMealFoods()) {
            MealFoodDetailsDto mealFoodDetailsDto = new MealFoodDetailsDto(
                    mealFood.getFood().getName(), // Numele alimentului
                    mealFood.getQuantity(), // Cantitatea
                    mealFood.getCalories(), // Caloriile
                    mealFood.getProtein(), // Proteinele
                    mealFood.getFat(), // Grăsimile
                    mealFood.getCarbs() // Carbohidrații
            );
            mealFoodDetails.add(mealFoodDetailsDto);
        }

        // Returnăm DTO-ul cu datele pentru AI
        return new MealAnalizeAiDto(meal.getMealType(), mealFoodDetails);
    }
}

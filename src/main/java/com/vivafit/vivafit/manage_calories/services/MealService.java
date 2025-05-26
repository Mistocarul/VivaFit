package com.vivafit.vivafit.manage_calories.services;

import com.vivafit.vivafit.authentification.entities.User;
import com.vivafit.vivafit.authentification.repositories.UserRepository;
import com.vivafit.vivafit.manage_calories.dto.*;
import com.vivafit.vivafit.manage_calories.entities.MealType;
import com.vivafit.vivafit.manage_calories.repositories.MealTypeRepository;
import com.vivafit.vivafit.manage_calories.responses.MealDetailsResponse;
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
    @Autowired
    private MealTypeRepository mealTypeRepository;

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

        Optional<MealFood> existingMealFoodOpt = meal.getMealFoods().stream()
                .filter(mf -> mf.getFood().getId().equals(food.getId()))
                .findFirst();

        MealFood mealFood;

        if (existingMealFoodOpt.isPresent()) {
            mealFood = existingMealFoodOpt.get();
            double newQuantity = mealFood.getQuantity() + request.getQuantity();
            double newFactor = newQuantity / 100;

            mealFood.setQuantity(newQuantity);
            mealFood.setCalories(Math.round(food.getCaloriesPer100g() * newFactor * 100.0) / 100.0);
            mealFood.setProtein(Math.round(food.getProteinPer100g() * newFactor * 100.0) / 100.0);
            mealFood.setFat(Math.round(food.getFatPer100g() * newFactor * 100.0) / 100.0);
            mealFood.setCarbs(Math.round(food.getCarbsPer100g() * newFactor * 100.0) / 100.0);

        } else {
            mealFood = MealFood.builder()
                    .meal(meal)
                    .food(food)
                    .quantity(request.getQuantity())
                    .calories(Math.round(food.getCaloriesPer100g() * factor * 100.0) / 100.0)
                    .protein(Math.round(food.getProteinPer100g() * factor * 100.0) / 100.0)
                    .fat(Math.round(food.getFatPer100g() * factor * 100.0) / 100.0)
                    .carbs(Math.round(food.getCarbsPer100g() * factor * 100.0) / 100.0)
                    .build();

            meal.getMealFoods().add(mealFood);
        }

        MealFood savedMealFood = mealFoodRepository.save(mealFood);

        return MealFoodResponse.builder()
                .id(savedMealFood.getId())
                .foodId(food.getId())
                .mealId(meal.getId())
                .foodName(food.getName())
                .quantity(savedMealFood.getQuantity())
                .calories(savedMealFood.getCalories())
                .protein(savedMealFood.getProtein())
                .fat(savedMealFood.getFat())
                .carbs(savedMealFood.getCarbs())
                .build();
    }

    @Transactional
    public void removeFoodFromMeal(RemoveFoodFromMealRequestDto request, User user) {
        Meal meal = mealRepository.findByDateAndUserAndMealType(request.getDate(), user, request.getMealType())
                .orElseThrow(() -> new RuntimeException("Meal not found for the given date, meal type, and user."));

        MealFood mealFood = mealFoodRepository.findByMealIdAndFoodId(meal.getId(), request.getFoodId())
                .orElseThrow(() -> new RuntimeException("Food not found in the specified meal."));

        mealFoodRepository.delete(mealFood);
    }

    @Transactional
    public MealFoodResponse updateFoodQuantityFromMeal(UpdateFoodQuantityFromMealRequestDto request, User user) {
        Meal meal = mealRepository.findByDateAndUserAndMealType(request.getDate(), user, request.getMealType())
                .orElseThrow(() -> new RuntimeException("Meal not found for the given date, meal type, and user."));

        MealFood mealFood = mealFoodRepository.findByMealIdAndFoodId(meal.getId(), request.getFoodId())
                .orElseThrow(() -> new RuntimeException("Food not found in the specified meal."));

        Food food = foodRepository.findById(request.getFoodId())
                .orElseThrow(() -> new RuntimeException("Food not found"));

        double factor = request.getQuantity() / 100;

        mealFood.setQuantity(request.getQuantity());
        mealFood.setCalories(Math.round(food.getCaloriesPer100g() * factor * 100.0) / 100.0);
        mealFood.setProtein(Math.round(food.getProteinPer100g() * factor * 100.0) / 100.0);
        mealFood.setFat(Math.round(food.getFatPer100g() * factor * 100.0) / 100.0);
        mealFood.setCarbs(Math.round(food.getCarbsPer100g() * factor * 100.0) / 100.0);

        MealFood updatedMealFood = mealFoodRepository.save(mealFood);

        return MealFoodResponse.builder()
                .id(updatedMealFood.getId())
                .foodId(food.getId())
                .mealId(meal.getId())
                .foodName(food.getName())
                .quantity(updatedMealFood.getQuantity())
                .calories(updatedMealFood.getCalories())
                .protein(updatedMealFood.getProtein())
                .fat(updatedMealFood.getFat())
                .carbs(updatedMealFood.getCarbs())
                .build();
    }

    @Transactional
    public List<MealDetailsResponse> getDetailsOfMeals(LocalDate date, User user) {
        List<Meal> meals = mealRepository.findByDateAndUser(date, user);

        if (meals.isEmpty()) {
            MealType mealType = mealTypeRepository.findByUser(user)
                    .orElseThrow(() -> new RuntimeException("Meal type not found for the user."));

            List<String> mealTypes = List.of(
                    mealType.getMealType1(),
                    mealType.getMealType2(),
                    mealType.getMealType3(),
                    mealType.getMealType4(),
                    mealType.getMealType5()
            );

            for (String type : mealTypes) {
                if (type != null && !type.isEmpty()) {
                    Meal newMeal = Meal.builder()
                            .date(date)
                            .mealType(type)
                            .user(user)
                            .mealFoods(new ArrayList<>())
                            .build();
                    Meal savedMeal = mealRepository.save(newMeal);
                    meals.add(savedMeal);
                }
            }
        }

        List<MealDetailsResponse> mealDetailsResponses = new ArrayList<>();

        for (Meal meal : meals) {
            List<MealFoodResponse> mealFoodResponses = meal.getMealFoods().stream()
                    .map(mealFood -> MealFoodResponse.builder()
                            .id(mealFood.getId())
                            .foodId(mealFood.getFood().getId())
                            .mealId(meal.getId())
                            .foodName(mealFood.getFood().getName())
                            .quantity(mealFood.getQuantity())
                            .calories(mealFood.getCalories())
                            .protein(mealFood.getProtein())
                            .fat(mealFood.getFat())
                            .carbs(mealFood.getCarbs())
                            .build())
                    .toList();

            MealDetailsResponse mealDetailsResponse = MealDetailsResponse.builder()
                    .mealType(meal.getMealType())
                    .mealFoods(mealFoodResponses)
                    .build();

            mealDetailsResponses.add(mealDetailsResponse);
        }

        return mealDetailsResponses;
    }

    @Transactional
    public MealAnalizeAiDto getMealAnalysisForAI(LocalDate mealDate, String mealType, Integer userId) {
        Optional<Meal> mealOptional = mealRepository.findByDateAndUserAndMealType(mealDate, userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found")), mealType);

        if (!mealOptional.isPresent()) {
            throw new RuntimeException("Meal not found for the given date, meal type, and user.");
        }

        Meal meal = mealOptional.get();
        List<MealFoodDetailsDto> mealFoodDetails = new ArrayList<>();
        for (MealFood mealFood : meal.getMealFoods()) {
            MealFoodDetailsDto mealFoodDetailsDto = new MealFoodDetailsDto(
                    mealFood.getFood().getName(),
                    mealFood.getQuantity(),
                    mealFood.getCalories(),
                    mealFood.getProtein(),
                    mealFood.getFat(),
                    mealFood.getCarbs()
            );
            mealFoodDetails.add(mealFoodDetailsDto);
        }

        return new MealAnalizeAiDto(meal.getMealType(), mealFoodDetails);
    }
}

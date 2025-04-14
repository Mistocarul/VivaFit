package com.vivafit.vivafit.manage_calories.services;

import com.vivafit.vivafit.authentification.entities.User;
import com.vivafit.vivafit.manage_calories.dto.BMRCalculatorDto;
import com.vivafit.vivafit.manage_calories.dto.BMRMacroDto;
import com.vivafit.vivafit.manage_calories.entities.BMRDetails;
import com.vivafit.vivafit.manage_calories.mappers.BMRDetailsMapper;
import com.vivafit.vivafit.manage_calories.repositories.BMRDetailsRepository;
import com.vivafit.vivafit.manage_calories.responses.BMRDetailsResponse;
import com.vivafit.vivafit.manage_calories.responses.BMRSimpleMessageResponse;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Getter
@Setter
@Service
public class BMRDetailsService {
    @Autowired
    private BMRDetailsRepository bmrDetailsRepository;
    @Autowired
    private BMRDetailsMapper bmrDetailsMapper;


    public void initializeBMRDetails(User user) {
        BMRDetails bmrDetails = BMRDetails.builder()
                .user(user)
                .gender(null)
                .age(null)
                .weight(null)
                .height(null)
                .activityLevel(null)
                .calorieDifference(null)
                .objective(null)
                .numberOfCalories(null)
                .gramsOfProteins(null)
                .gramsOfCarbs(null)
                .gramsOfFats(null)
                .build();
        bmrDetailsRepository.save(bmrDetails);
    }

    public BMRDetailsResponse getBMRDetails(User user) {
        BMRDetails bmrDetails = bmrDetailsRepository.findByUserUsername(user.getUsername());
        return bmrDetails != null ? bmrDetailsMapper.toResponse(bmrDetails) : null;
    }

    public BMRDetailsResponse calculateBMR(User user, BMRCalculatorDto bmrCalculatorDto) {
        Integer numberOfCalories = 0;
        Integer gramsOfProteins = 0;
        Integer gramsOfFats = 0;
        Integer gramsOfCarbs = 0;
        Integer calorieDifference = 0;
        String gender = bmrCalculatorDto.getGender();
        Integer age = bmrCalculatorDto.getAge();
        Double height = bmrCalculatorDto.getHeightInCm();
        Double weight = bmrCalculatorDto.getWeightInKg();
        String activityLevel = bmrCalculatorDto.getActivityLevelPerWeek();
        String objective = bmrCalculatorDto.getObjective();

        if (gender.equals("Masculin")){
            numberOfCalories = (int) (10 * weight + 6.25 * height - 5 * age + 5);
        }
        if (gender.equals("Feminin")){
            numberOfCalories = (int) (10 * weight + 6.25 * height - 5 * age - 161);
        }
        switch (activityLevel) {
            case "Sedentar":
                numberOfCalories = (int) (numberOfCalories * 1.2);
                break;
            case "Putin activ":
                numberOfCalories = (int) (numberOfCalories * 1.375);
                break;
            case "Moderat activ":
                numberOfCalories = (int) (numberOfCalories * 1.55);
                break;
            case "Foarte activ":
                numberOfCalories = (int) (numberOfCalories * 1.725);
                break;
            case "Extrem de activ":
                numberOfCalories = (int) (numberOfCalories * 1.9);
                break;
            default:
                break;
        }
        switch (objective) {
            case "Scadere":
                calorieDifference = -500;
                break;
            case "Mentinere":
                calorieDifference = 0;
                break;
            case "Crestere":
                calorieDifference = 500;
                break;
            default:
                break;
        }
        gramsOfProteins = (int) (((numberOfCalories + calorieDifference) * 0.3) / 4); // 1g protein = 4 calories
        gramsOfFats = (int) (((numberOfCalories + calorieDifference) * 0.25) / 9); // 1g fat = 9 calories
        gramsOfCarbs = (int) (((numberOfCalories + calorieDifference) * 0.45) / 4); // 1g carb = 4 calories

        BMRDetailsResponse bmrDetailsResponse = new BMRDetailsResponse();
        bmrDetailsResponse.setGender(gender);
        bmrDetailsResponse.setAge(age);
        bmrDetailsResponse.setHeight(height);
        bmrDetailsResponse.setWeight(weight);
        bmrDetailsResponse.setActivityLevel(activityLevel);
        bmrDetailsResponse.setCalorieDifference(calorieDifference);
        bmrDetailsResponse.setObjective(objective);
        bmrDetailsResponse.setNumberOfCalories(numberOfCalories);
        bmrDetailsResponse.setGramsOfProteins(gramsOfProteins);
        bmrDetailsResponse.setGramsOfFats(gramsOfFats);
        bmrDetailsResponse.setGramsOfCarbs(gramsOfCarbs);

        BMRDetails existingBmrDetails = bmrDetailsRepository.findByUserUsername(user.getUsername());
        if (existingBmrDetails != null) {
            existingBmrDetails.setGender(gender);
            existingBmrDetails.setAge(age);
            existingBmrDetails.setWeight(weight);
            existingBmrDetails.setHeight(height);
            existingBmrDetails.setActivityLevel(activityLevel);
            existingBmrDetails.setCalorieDifference(calorieDifference);
            existingBmrDetails.setObjective(objective);
            existingBmrDetails.setNumberOfCalories(numberOfCalories);
            existingBmrDetails.setGramsOfProteins(gramsOfProteins);
            existingBmrDetails.setGramsOfFats(gramsOfFats);
            existingBmrDetails.setGramsOfCarbs(gramsOfCarbs);

            bmrDetailsRepository.save(existingBmrDetails);
        } else {
            throw new RuntimeException("BMRDetails not found for user: " + user.getUsername());
        }
        bmrDetailsResponse.setMessage("BMR details calculated successfully");
        return bmrDetailsResponse;
    }

    public BMRSimpleMessageResponse calculateBMRMacros(User user, BMRMacroDto bmrMacroDto) {
        Integer calorieDifference = bmrMacroDto.getCalorieDifference();
        Integer gramsOfProteins = bmrMacroDto.getGramsOfProteins();
        Integer gramsOfFats = bmrMacroDto.getGramsOfFats();
        Integer gramsOfCarbs = bmrMacroDto.getGramsOfCarbs();

        Integer numberOfCaloriesFromMacros = (gramsOfProteins * 4) + (gramsOfFats * 9) + (gramsOfCarbs * 4);
        if (numberOfCaloriesFromMacros == 0) {
            throw new IllegalArgumentException("Total calories from macros cannot be zero");
        }

        Double procentageOfProteins = ((gramsOfProteins * 4.0) / numberOfCaloriesFromMacros) * 100;
        Double procentageOfFats = ((gramsOfFats * 9.0) / numberOfCaloriesFromMacros) * 100;
        Double procentageOfCarbs = ((gramsOfCarbs * 4.0) / numberOfCaloriesFromMacros) * 100;

        BMRDetails existingBmrDetails = bmrDetailsRepository.findByUserUsername(user.getUsername());
        if (existingBmrDetails == null) {
            throw new RuntimeException("BMRDetails not found for user: " + user.getUsername());
        }

        Integer numerOfCalories = existingBmrDetails.getNumberOfCalories();
        Integer numberOfCaloriesWithDifference = numerOfCalories + calorieDifference;
        if (numberOfCaloriesWithDifference < 0) {
            throw new IllegalArgumentException("Number of calories with difference cannot be negative");
        }

        gramsOfProteins = (int) Math.round((numberOfCaloriesWithDifference * procentageOfProteins) / 100 / 4.0);
        gramsOfFats = (int) Math.round((numberOfCaloriesWithDifference * procentageOfFats) / 100 / 9.0);
        gramsOfCarbs = (int) Math.round((numberOfCaloriesWithDifference * procentageOfCarbs) / 100 / 4.0);

        existingBmrDetails.setCalorieDifference(calorieDifference);
        existingBmrDetails.setGramsOfProteins(gramsOfProteins);
        existingBmrDetails.setGramsOfFats(gramsOfFats);
        existingBmrDetails.setGramsOfCarbs(gramsOfCarbs);

        bmrDetailsRepository.save(existingBmrDetails);

        BMRSimpleMessageResponse bmrSimpleMessageResponse = new BMRSimpleMessageResponse();
        bmrSimpleMessageResponse.setMessage("BMR macros calculated successfully");
        return bmrSimpleMessageResponse;
    }
}

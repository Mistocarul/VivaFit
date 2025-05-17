package com.vivafit.vivafit.manage_calories.services;

import com.vivafit.vivafit.authentification.entities.User;
import com.vivafit.vivafit.manage_calories.dto.MealTypeUpdateRequestDto;
import com.vivafit.vivafit.manage_calories.entities.MealType;
import com.vivafit.vivafit.manage_calories.repositories.MealTypeRepository;
import com.vivafit.vivafit.manage_calories.responses.MealTypeResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Getter
@Setter
@Service
public class MealTypeService {
    @Autowired
    private MealTypeRepository mealTypeRepository;

    public void initializeMealType(User user) {
        MealType mealType = MealType.builder()
                .user(user)
                .mealType1("Mic dejun")
                .mealType2("Pranz")
                .mealType3("Cina")
                .mealType4("Gustare")
                .mealType5(null)
                .build();
        mealTypeRepository.save(mealType);
    }

    public MealTypeResponse getMealTypesByUser(User user){
        MealType mealType = mealTypeRepository.findByUser(user)
                .orElseThrow(() -> new EntityNotFoundException("Meal types not found for user " + user.getId()));

        return mapToResponse(mealType);
    }

    public MealTypeResponse updateMealTypes(User user, MealTypeUpdateRequestDto updateRequestDto) {
        MealType mealType = mealTypeRepository.findByUser(user)
                .orElseThrow(() -> new EntityNotFoundException("Meal types not found for user " + user.getId()));

        mealType.setMealType1(updateRequestDto.getMealType1());
        mealType.setMealType2(updateRequestDto.getMealType2());
        mealType.setMealType3(updateRequestDto.getMealType3());
        mealType.setMealType4(updateRequestDto.getMealType4());
        mealType.setMealType5(updateRequestDto.getMealType5());

        MealType updated = mealTypeRepository.save(mealType);
        return mapToResponse(updated);
    }

    private MealTypeResponse mapToResponse(MealType mealType) {
        return MealTypeResponse.builder()
                .mealType1(mealType.getMealType1())
                .mealType2(mealType.getMealType2())
                .mealType3(mealType.getMealType3())
                .mealType4(mealType.getMealType4())
                .mealType5(mealType.getMealType5())
                .build();
    }
}

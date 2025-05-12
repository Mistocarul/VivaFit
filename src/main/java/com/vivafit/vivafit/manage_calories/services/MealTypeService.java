package com.vivafit.vivafit.manage_calories.services;

import com.vivafit.vivafit.authentification.entities.User;
import com.vivafit.vivafit.manage_calories.entities.MealType;
import com.vivafit.vivafit.manage_calories.repositories.MealTypeRepository;
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
}

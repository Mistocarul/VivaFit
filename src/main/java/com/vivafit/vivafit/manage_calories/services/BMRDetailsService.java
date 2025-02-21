package com.vivafit.vivafit.manage_calories.services;

import com.vivafit.vivafit.authentification.entities.User;
import com.vivafit.vivafit.manage_calories.entities.BMRDetails;
import com.vivafit.vivafit.manage_calories.repositories.BMRDetailsRepository;
import lombok.Getter;
import lombok.Setter;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Getter
@Setter
@Service
public class BMRDetailsService {
    @Autowired
    private BMRDetailsRepository bmrDetailsRepository;


    public void initializeBMRDetails(User user) {
        BMRDetails bmrDetails = new BMRDetails();
        bmrDetails.setUser(user);
        bmrDetails.setGender(null);
        bmrDetails.setAge(null);
        bmrDetails.setWeight(null);
        bmrDetails.setHeight(null);
        bmrDetails.setActivityLevel(null);
        bmrDetails.setCalorieDifference(null);
        bmrDetails.setObjective(null);
        bmrDetails.setNumberOfCalories(null);
        bmrDetails.setGramsOfProteins(null);
        bmrDetails.setGramsOfCarbs(null);
        bmrDetails.setGramsOfFats(null);
        bmrDetailsRepository.save(bmrDetails);
    }
}

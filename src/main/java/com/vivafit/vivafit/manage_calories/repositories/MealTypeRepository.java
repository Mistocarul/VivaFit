package com.vivafit.vivafit.manage_calories.repositories;

import com.vivafit.vivafit.authentification.entities.User;
import com.vivafit.vivafit.manage_calories.entities.MealType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MealTypeRepository extends JpaRepository<MealType, Long> {
    Optional<MealType> findByUser(User user);
}

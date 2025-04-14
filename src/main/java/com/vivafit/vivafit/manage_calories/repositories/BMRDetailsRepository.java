package com.vivafit.vivafit.manage_calories.repositories;

import com.vivafit.vivafit.manage_calories.entities.BMRDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BMRDetailsRepository extends JpaRepository<BMRDetails, Long> {
    BMRDetails findByUserUsername(String username);
}

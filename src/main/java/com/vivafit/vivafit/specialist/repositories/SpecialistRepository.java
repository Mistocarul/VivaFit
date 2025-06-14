package com.vivafit.vivafit.specialist.repositories;

import com.vivafit.vivafit.specialist.entities.Specialist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SpecialistRepository extends JpaRepository<Specialist, Integer> {
    Optional<Specialist> findByUserId(Integer userId);
    Optional<Specialist> findByEmail(String email);
    Optional<Specialist> findByPhoneNumber(String phoneNumber);
}

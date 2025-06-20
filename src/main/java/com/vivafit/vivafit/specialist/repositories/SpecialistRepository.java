package com.vivafit.vivafit.specialist.repositories;

import com.vivafit.vivafit.specialist.entities.Specialist;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface SpecialistRepository extends JpaRepository<Specialist, Integer> {
    Optional<Specialist> findByUserId(Integer userId);
    Optional<Specialist> findByEmail(String email);
    Optional<Specialist> findByPhoneNumber(String phoneNumber);
    Page<Specialist> findByProfession(String profession, Pageable pageable);

    @Query("SELECT s FROM Specialist s WHERE LOWER(s.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    Page<Specialist> findByNameContainingIgnoreCase(@Param("name") String name, Pageable pageable);
}

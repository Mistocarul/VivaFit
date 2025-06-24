package com.vivafit.vivafit.weight.repositories;

import com.vivafit.vivafit.weight.entities.Weight;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface WeightRepository extends JpaRepository<Weight, Integer> {
    Optional<Weight> findByUserIdAndDate(Integer userId, LocalDate date);
    Optional<List<Weight>> findByUserIdAndDateBetween(Integer userId, LocalDate startDate, LocalDate endDate);
    List<Weight> findTop30ByUserIdOrderByDateDesc(Integer userId);
}

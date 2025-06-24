package com.vivafit.vivafit.specialist.repositories;

import com.vivafit.vivafit.specialist.entities.MessagesSpecialists;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessagesSpecialistRepository extends JpaRepository<MessagesSpecialists, Integer> {
    List<MessagesSpecialists> findAllBySpecialistId(Integer specialistId);
}

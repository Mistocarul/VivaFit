package com.vivafit.vivafit.authentification.repositories;

import com.vivafit.vivafit.authentification.entities.ConfirmationCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ConfirmationCodeRepository extends JpaRepository<ConfirmationCode, Long> {
    ConfirmationCode findByUsername(String username);
}

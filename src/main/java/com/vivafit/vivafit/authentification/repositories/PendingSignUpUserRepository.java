package com.vivafit.vivafit.authentification.repositories;

import com.vivafit.vivafit.authentification.entities.PendingSignUpUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PendingSignUpUserRepository extends JpaRepository<PendingSignUpUser, Long> {
    PendingSignUpUser findByUsername(String username);
    PendingSignUpUser findByEmail(String email);
    PendingSignUpUser findByPhoneNumber(String phoneNumber);
}

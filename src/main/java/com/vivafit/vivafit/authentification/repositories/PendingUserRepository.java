package com.vivafit.vivafit.authentification.repositories;

import com.vivafit.vivafit.authentification.entities.PendingUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PendingUserRepository  extends JpaRepository<PendingUser, Long> {
    PendingUser findByUsername(String username);
    PendingUser findByEmail(String email);
    PendingUser findByPhoneNumber(String phoneNumber);
}

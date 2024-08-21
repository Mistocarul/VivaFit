package com.vivafit.vivafit.authentification.repositories;

import com.vivafit.vivafit.authentification.entities.PendingSignInUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PendingSignInUserRepository extends JpaRepository<PendingSignInUser, Long> {
    PendingSignInUser findByIdentifier(String identifier);
}

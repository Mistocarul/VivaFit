package com.vivafit.vivafit.authentification.repositories;

import com.vivafit.vivafit.authentification.entities.SignInToken;
import com.vivafit.vivafit.authentification.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SignInTokenRepository extends JpaRepository<SignInToken, Long> {
    Optional<SignInToken> findByToken(String token);
    Optional<SignInToken> findByUser(User user);
    List<SignInToken> findAllByExpiryDateBefore(LocalDateTime expiryDate);
}

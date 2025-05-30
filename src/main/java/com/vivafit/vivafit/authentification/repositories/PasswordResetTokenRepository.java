package com.vivafit.vivafit.authentification.repositories;

import com.vivafit.vivafit.authentification.entities.PasswordResetToken;
import com.vivafit.vivafit.authentification.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByToken(String token);
    Optional<PasswordResetToken> findByUser(User user);
    void deleteByUser(User user);
    List<PasswordResetToken> findAllByExpiryDateBefore(LocalDateTime expiryDate);
}

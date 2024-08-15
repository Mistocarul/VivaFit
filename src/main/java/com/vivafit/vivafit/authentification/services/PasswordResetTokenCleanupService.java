package com.vivafit.vivafit.authentification.services;

import com.vivafit.vivafit.authentification.entities.PasswordResetToken;
import com.vivafit.vivafit.authentification.repositories.PasswordResetTokenRepository;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class PasswordResetTokenCleanupService {
    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    private ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);

    @PostConstruct
    public void startScheduler(){
        scheduledExecutorService.scheduleAtFixedRate(this::removeExpiredTokens, 0, 30, TimeUnit.MINUTES);
    }

    @PreDestroy
    public void stopScheduler(){
        scheduledExecutorService.shutdown();
    }

    private void removeExpiredTokens(){
        LocalDateTime now = LocalDateTime.now();
        List<PasswordResetToken> expiredTokens = passwordResetTokenRepository.findAllByExpiryDateBefore(now);
        if (!expiredTokens.isEmpty()){
            passwordResetTokenRepository.deleteAll(expiredTokens);
        }
    }
}

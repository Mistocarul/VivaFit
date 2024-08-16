package com.vivafit.vivafit.authentification.services;

import com.vivafit.vivafit.authentification.entities.SignInToken;
import com.vivafit.vivafit.authentification.entities.User;
import com.vivafit.vivafit.authentification.repositories.SignInTokenRepository;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class SignInTokenService {
    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;
    @Autowired
    private SignInTokenRepository signInTokenRepository;

    private ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);

    public void registerToken(User user, String token, LocalDateTime expiryDate) {
        SignInToken signInToken = new SignInToken();
        signInToken.setUser(user);
        signInToken.setToken(token);
        signInToken.setExpiryDate(expiryDate);
        signInTokenRepository.save(signInToken);
    }

    public void unregisterToken(User user) {
        SignInToken signInToken = signInTokenRepository.findByUser(user).orElse(null);
        if (signInToken != null) {
            signInTokenRepository.delete(signInToken);
        }
    }

    public String getToken(User user) {
        SignInToken signInToken = signInTokenRepository.findByUser(user).orElse(null);
        if (signInToken != null) {
            return signInToken.getToken();
        }
        return null;
    }

    public boolean isTokenActive(String token) {
        SignInToken signInToken = signInTokenRepository.findByToken(token).orElse(null);
        if (signInToken != null) {
            return signInToken.getExpiryDate().isAfter(LocalDateTime.now());
        }
        return false;
    }

    public void notifyUserOfDesconnection(User user) {
        String username = user.getUsername();
        System.out.println("User " + username + " has been disconnected.");
        simpMessagingTemplate.convertAndSendToUser(username,"/queue/disconnect", "You have been disconnected.");
    }

    @PostConstruct
    public void startTokenCleanup() {
        scheduledExecutorService.scheduleAtFixedRate(this::cleanupExpiredTokens, 0, 1, TimeUnit.HOURS);
    }

    @PreDestroy
    public void stopTokenCleanup() {
        scheduledExecutorService.shutdown();
    }

    private void cleanupExpiredTokens() {
        List<SignInToken> expiredTokens = signInTokenRepository.findAllByExpiryDateBefore(LocalDateTime.now());
        if (!expiredTokens.isEmpty()) {
            signInTokenRepository.deleteAll(expiredTokens);
        }
    }
}

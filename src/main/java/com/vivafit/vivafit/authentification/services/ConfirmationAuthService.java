package com.vivafit.vivafit.authentification.services;

import com.vivafit.vivafit.authentification.entities.PendingUser;
import com.vivafit.vivafit.authentification.entities.ConfirmationCode;
import com.vivafit.vivafit.authentification.repositories.ConfirmationCodeRepository;
import com.vivafit.vivafit.authentification.repositories.PendingUserRepository;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class ConfirmationAuthService {
    @Autowired
    private PendingUserRepository pendingUserRepository;
    @Autowired
    private ConfirmationCodeRepository confirmationCodeRepository;

    private ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);

    @PostConstruct
    public void startCleanUpTask() {
        scheduledExecutorService.scheduleAtFixedRate(this::cleanupOldEntries,0, 1, TimeUnit.MINUTES);
    }

    @PreDestroy
    public void stopCleanUpTask() {
        scheduledExecutorService.shutdown();
        pendingUserRepository.deleteAll();
        confirmationCodeRepository.deleteAll();
    }

    public void cleanupOldEntries(){
        LocalDateTime now = LocalDateTime.now();
        pendingUserRepository.findAll().forEach(pendingUser -> {
            ConfirmationCode confirmationCode = confirmationCodeRepository.findByUsername(pendingUser.getUsername());
            if(confirmationCode != null && Duration.between(confirmationCode.getCreationTime(), now).toMinutes() > 30){
                confirmationCodeRepository.delete(confirmationCode);
                pendingUserRepository.delete(pendingUser);
            }
        });
    }

    public void addPendingUser(PendingUser pendingUser){
        pendingUserRepository.save(pendingUser);
    }

    public PendingUser getPendingUser(String username){
        PendingUser pendingUser = pendingUserRepository.findByUsername(username);
        if(pendingUser != null){
            return pendingUser;
        }
        return null;
    }

    public void removePendingUser(PendingUser user){
        PendingUser pendingUser = pendingUserRepository.findByUsername(user.getUsername());
        if(pendingUser != null){
            pendingUserRepository.delete(pendingUser);
        }
    }

    public void addConfirmationCode(String username, ConfirmationCode confirmationCode){
        ConfirmationCode newConfirmationCode = new ConfirmationCode();
        newConfirmationCode.setUsername(username);
        newConfirmationCode.setCode(confirmationCode.getCode());
        newConfirmationCode.setCreationTime(confirmationCode.getCreationTime());
        confirmationCodeRepository.save(newConfirmationCode);
    }

    public ConfirmationCode getConfirmationCode(String username){
        return confirmationCodeRepository.findByUsername(username);
    }

    public void removeConfirmationCode(String username){
        ConfirmationCode confirmationCode = confirmationCodeRepository.findByUsername(username);
        if(confirmationCode != null){
            confirmationCodeRepository.delete(confirmationCode);
        }
    }
}

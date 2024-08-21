package com.vivafit.vivafit.authentification.services;

import com.vivafit.vivafit.authentification.entities.PendingSignUpUser;
import com.vivafit.vivafit.authentification.entities.ConfirmationCode;
import com.vivafit.vivafit.authentification.repositories.ConfirmationCodeRepository;
import com.vivafit.vivafit.authentification.repositories.PendingSignUpUserRepository;
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
    private PendingSignUpUserRepository pendingSignUpUserRepository;
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
        pendingSignUpUserRepository.deleteAll();
        confirmationCodeRepository.deleteAll();
    }

    public void cleanupOldEntries(){
        LocalDateTime now = LocalDateTime.now();
        pendingSignUpUserRepository.findAll().forEach(pendingSignUpUser -> {
            ConfirmationCode confirmationCode = confirmationCodeRepository.findByUsername(pendingSignUpUser.getUsername());
            if(confirmationCode != null && Duration.between(confirmationCode.getCreationTime(), now).toMinutes() > 30){
                confirmationCodeRepository.delete(confirmationCode);
                pendingSignUpUserRepository.delete(pendingSignUpUser);
            }
        });
    }

    public void addPendingSignUpUser(PendingSignUpUser pendingSignUpUser){
        pendingSignUpUserRepository.save(pendingSignUpUser);
    }

    public PendingSignUpUser getPendingSignUpUser(String username){
        PendingSignUpUser pendingSignUpUser = pendingSignUpUserRepository.findByUsername(username);
        if(pendingSignUpUser != null){
            return pendingSignUpUser;
        }
        return null;
    }

    public void removePendingSignUpUser(PendingSignUpUser user){
        PendingSignUpUser pendingSignUpUser = pendingSignUpUserRepository.findByUsername(user.getUsername());
        if(pendingSignUpUser != null){
            pendingSignUpUserRepository.delete(pendingSignUpUser);
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

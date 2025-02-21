package com.vivafit.vivafit.authentification.services;

import com.vivafit.vivafit.authentification.dto.LoginUserDto;
import com.vivafit.vivafit.authentification.entities.*;
import com.vivafit.vivafit.authentification.repositories.*;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class ConfirmationAuthService {
    @Autowired
    private PendingSignUpUserRepository pendingSignUpUserRepository;
    @Autowired
    private ConfirmationCodeRepository confirmationCodeRepository;
    @Autowired
    private PendingSignInUserRepository pendingSignInUserRepository;
    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;
    @Autowired
    private EncryptionDataService encryptionDataService;
    @Autowired
    private UserRepository userRepository;

    private ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);

    @PostConstruct
    public void startCleanUpTask() {
        scheduledExecutorService.scheduleAtFixedRate(this::cleanupOldEntries,0, 30, TimeUnit.MINUTES);
    }

    @PreDestroy
    public void stopCleanUpTask() {
        scheduledExecutorService.shutdown();
        pendingSignUpUserRepository.deleteAll();
        confirmationCodeRepository.deleteAll();
        pendingSignInUserRepository.deleteAll();
        passwordResetTokenRepository.deleteAll();
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
        List<PasswordResetToken> expiredTokens = passwordResetTokenRepository.findAllByExpiryDateBefore(now);
        if (!expiredTokens.isEmpty()){
            passwordResetTokenRepository.deleteAll(expiredTokens);
        }
        pendingSignInUserRepository.findAll().forEach(pendingSignInUser -> {
            String identifier = pendingSignInUser.getIdentifier();
            User user = new User();
            if(identifier.contains("@")){
                user = userRepository.findByEmail(identifier).orElse(null);
            }else{
                user = userRepository.findByUsername(identifier).orElse(null);
            }
            ConfirmationCode confirmationCode = confirmationCodeRepository.findByUsername(user.getUsername());
            if(confirmationCode != null && Duration.between(confirmationCode.getCreationTime(), now).toMinutes() > 30){
                confirmationCodeRepository.delete(confirmationCode);
                pendingSignInUserRepository.delete(pendingSignInUser);
            }
        });
    }

    public void addPendingSignUpUser(PendingSignUpUser pendingSignUpUser){
        if(pendingSignUpUserRepository.findByUsername(pendingSignUpUser.getUsername()) != null){
            pendingSignUpUserRepository.delete(pendingSignUpUserRepository.findByUsername(pendingSignUpUser.getUsername()));
        }
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
        if(confirmationCodeRepository.findByUsername(username) != null){
            confirmationCodeRepository.delete(confirmationCodeRepository.findByUsername(username));
        }
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

    public void addPendingSignInUser(PendingSignInUser pendingSignInUser){
        if(pendingSignInUserRepository.findByIdentifier(pendingSignInUser.getIdentifier()) != null){
            pendingSignInUserRepository.delete(pendingSignInUserRepository.findByIdentifier(pendingSignInUser.getIdentifier()));
        }
        String password = pendingSignInUser.getPassword();
        try {
            pendingSignInUser.setPassword(encryptionDataService.encrypt(password));
        } catch (Exception e) {
            e.printStackTrace();
        }
        pendingSignInUserRepository.save(pendingSignInUser);
    }

    public PendingSignInUser getPendingSignInUser(String identifier){
        return pendingSignInUserRepository.findByIdentifier(identifier);
    }

    public void removePendingSignInUser(PendingSignInUser pendingSignInUser){
        PendingSignInUser pendingSignInUserCopy = pendingSignInUserRepository.findByIdentifier(pendingSignInUser.getIdentifier());
        if(pendingSignInUserCopy != null){
            pendingSignInUserRepository.delete(pendingSignInUserCopy);
        }
    }
}

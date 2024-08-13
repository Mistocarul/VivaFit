package com.vivafit.vivafit.authentification.services;

import com.vivafit.vivafit.authentification.entities.User;
import com.vivafit.vivafit.authentification.models.ConfirmationCode;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class ConfirmationAuthService {
    private Map<String, User> pendingUsers = new ConcurrentHashMap<>();
    private Map<String, ConfirmationCode> confirmationCodes = new ConcurrentHashMap<>();
    private Map<String, String> profilePictures = new ConcurrentHashMap<>();
    private ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);

    @PostConstruct
    public void startCleanUpTask() {
        scheduledExecutorService.scheduleAtFixedRate(this::cleanupOldEntries,0, 1, TimeUnit.MINUTES);
    }

    @PreDestroy
    public void stopCleanUpTask() {
        scheduledExecutorService.shutdown();
    }

    public void cleanupOldEntries(){
        LocalDateTime now = LocalDateTime.now();
        pendingUsers.entrySet().removeIf(entry -> {
            ConfirmationCode confirmationCode = confirmationCodes.get(entry.getKey());
            boolean mustRemove;
            if(confirmationCode != null && Duration.between(confirmationCode.getCreationTime(), now).toMinutes() > 30){
                confirmationCodes.remove(entry.getKey());
                profilePictures.remove(entry.getKey());
                mustRemove = true;
            } else {
                mustRemove = false;
            }
            return mustRemove;
        });
    }

    public void addPendingUser(String username, User user){
        pendingUsers.put(username, user);
    }

    public User getPendingUser(String username){
        return pendingUsers.get(username);
    }

    public void removePendingUser(String username){
        pendingUsers.remove(username);
    }

    public void addConfirmationCode(String username, ConfirmationCode confirmationCode){
        confirmationCodes.put(username, confirmationCode);
    }

    public ConfirmationCode getConfirmationCode(String username){
        return confirmationCodes.get(username);
    }

    public void removeConfirmationCode(String username){
        confirmationCodes.remove(username);
    }

    public void addProfilePicture(String username, String profilePicture){
        profilePictures.put(username, profilePicture);
    }

    public String getProfilePicture(String username){
        return profilePictures.get(username);
    }

    public void removeProfilePicture(String username){
        profilePictures.remove(username);
    }

}

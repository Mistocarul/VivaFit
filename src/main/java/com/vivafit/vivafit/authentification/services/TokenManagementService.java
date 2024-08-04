package com.vivafit.vivafit.authentification.services;

import com.vivafit.vivafit.authentification.controllers.WebSocketController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TokenManagementService {
    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    private Map<String, String> activeTokens = new ConcurrentHashMap<>();

    public void registerToken(String username, String token) {
        activeTokens.put(username, token);
    }

    public void unregisterToken(String username) {
        activeTokens.remove(username);
    }

    public String getToken(String username) {
        return activeTokens.get(username);
    }

    public boolean isTokenActive(String token) {
        return activeTokens.containsValue(token);
    }

    public void notifyUserOfDesconnection(String username) {
        System.out.println("User " + username + " has been disconnected.");
        simpMessagingTemplate.convertAndSendToUser(username,"/queue/disconnect", "You have been disconnected.");
    }
}

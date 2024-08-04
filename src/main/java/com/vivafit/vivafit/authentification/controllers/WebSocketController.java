package com.vivafit.vivafit.authentification.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketController {
    @MessageMapping
    @SendTo("/queue/disconnect")
    public String notifyUserOfDesconnection(String username, String message) {
        System.out.println("User " + username + " has been disconnected.");
        return message;
    }
}

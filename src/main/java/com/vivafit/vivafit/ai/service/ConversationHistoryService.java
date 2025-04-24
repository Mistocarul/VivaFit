package com.vivafit.vivafit.ai.service;

import org.springframework.ai.chat.messages.Message;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ConversationHistoryService {
    private Map<String, List<Message>> conversationHistory = new ConcurrentHashMap<>();

    public List<Message> getConversationHistory(String token) {
        return conversationHistory.getOrDefault(token, new ArrayList<>());
    }

    public void addMessageToHistory(String token, Message message) {
        conversationHistory.computeIfAbsent(token, k -> new ArrayList<>()).add(message);
    }

    public void clearConversationHistory(String token) {
        conversationHistory.remove(token);
    }
}

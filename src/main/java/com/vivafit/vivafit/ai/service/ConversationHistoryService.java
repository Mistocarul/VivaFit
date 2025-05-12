package com.vivafit.vivafit.ai.service;

import org.springframework.ai.chat.messages.Message;
import org.springframework.stereotype.Service;
import reactor.core.publisher.FluxSink;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ConversationHistoryService {
    private final Map<String, List<Message>> conversationHistory = new ConcurrentHashMap<>();
    private final Map<String, FluxSink<String>> activeSinks = new ConcurrentHashMap<>();

    public List<Message> getConversationHistory(String token) {
        return conversationHistory.getOrDefault(token, new ArrayList<>());
    }

    public void addMessageToHistory(String token, Message message) {
        conversationHistory.computeIfAbsent(token, k -> new ArrayList<>()).add(message);
    }

    public void clearConversationHistory(String token) {
        conversationHistory.remove(token);
    }

    public void registerStream(String token, FluxSink<String> sink) {
        activeSinks.put(token, sink);
    }

    public void stopStream(String token) {
        FluxSink<String> sink = activeSinks.remove(token);
        if (sink != null) {
            sink.complete();
        }
    }

    public void unregisterStream(String token) {
        activeSinks.remove(token);
    }

    public boolean isStreamActive(String token) {
        return activeSinks.containsKey(token);
    }
}

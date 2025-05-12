package com.vivafit.vivafit.ai.controllers;

import com.vivafit.vivafit.ai.dto.ChatRequestDto;
import com.vivafit.vivafit.ai.responses.ChatResponse;
import com.vivafit.vivafit.ai.service.AiChatService;
import com.vivafit.vivafit.ai.service.ConversationHistoryService;
import com.vivafit.vivafit.authentification.entities.User;
import com.vivafit.vivafit.authentification.services.JwtService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@Validated
@RestController
@RequestMapping("/api/chat")
public class AiChatController {
    @Autowired
    private JwtService jwtService;
    @Autowired
    private AiChatService aiChatService;
    @Autowired
    private ConversationHistoryService conversationHistoryService;

    @PostMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> streamChat(
            @RequestHeader("Authorization") String authorizationHeader,
            @Valid @RequestBody ChatRequestDto chatRequestDto) {

        User currentUser = jwtService.validateAndGetCurrentUser(authorizationHeader);
        String token = authorizationHeader.substring(7);

        return aiChatService.chatStream(
                        currentUser,
                        token,
                        chatRequestDto.getPrompt(),
                        chatRequestDto.getCategory(),
                        chatRequestDto.getMealType(),
                        chatRequestDto.getMealDate())
                .map(response -> ServerSentEvent.builder(response).build());
    }

    @DeleteMapping("/reset-history")
    public ChatResponse resetHistory(
            @RequestHeader("Authorization") String authorizationHeader) {
        User currentUser = jwtService.validateAndGetCurrentUser(authorizationHeader);
        String token = authorizationHeader.substring(7);
        conversationHistoryService.clearConversationHistory(token);
        return new ChatResponse("Conversation history cleared");
    }

    @PostMapping("/stop-stream")
    public ChatResponse stopStream(
            @RequestHeader("Authorization") String authorizationHeader) {
        User currentUser = jwtService.validateAndGetCurrentUser(authorizationHeader);
        String token = authorizationHeader.substring(7);

        if (conversationHistoryService.isStreamActive(token)) {
            conversationHistoryService.stopStream(token);
            return new ChatResponse("Stream stoped successfully.");
        } else {
            return new ChatResponse("Stream is not active.");
        }
    }
}

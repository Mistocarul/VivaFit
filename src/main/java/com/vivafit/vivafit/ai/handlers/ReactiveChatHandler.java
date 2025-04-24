package com.vivafit.vivafit.ai.handlers;

import com.vivafit.vivafit.ai.dto.ChatRequestDto;
import com.vivafit.vivafit.ai.service.AiChatService;
import com.vivafit.vivafit.ai.service.ConversationHistoryService;
import com.vivafit.vivafit.authentification.entities.User;
import com.vivafit.vivafit.authentification.services.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class ReactiveChatHandler {

    @Autowired
    private AiChatService aiChatService;
    @Autowired
    private ConversationHistoryService conversationHistoryService;
    @Autowired
    private JwtService jwtService;

    public Mono<ServerResponse> streamChat(ServerRequest request) {
        return request.bodyToMono(ChatRequestDto.class)
                .flatMap(dto -> {
                    User currentUser = jwtService.validateAndGetCurrentUser(
                            request.headers().firstHeader("Authorization"));
                    String token = request.headers().firstHeader("Authorization").substring(7);

                    Flux<ServerSentEvent<String>> eventStream = aiChatService.chatStream(
                                    token,
                                    dto.getPrompt(),
                                    dto.getCategory(),
                                    dto.getFoodDate(),
                                    dto.getFoodName())
                            .map(response -> ServerSentEvent.builder(response).build());

                    return ServerResponse.ok()
                            .contentType(MediaType.TEXT_EVENT_STREAM)
                            .body(eventStream, ServerSentEvent.class);
                });
    }

    public Mono<ServerResponse> resetHistory(ServerRequest request) {
        User currentUser = jwtService.validateAndGetCurrentUser(
                request.headers().firstHeader("Authorization"));
        String token = request.headers().firstHeader("Authorization").substring(7);
        conversationHistoryService.clearConversationHistory(token);
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("Conversation history cleared");
    }
}
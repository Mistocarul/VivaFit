package com.vivafit.vivafit.ai.configurations;

import com.vivafit.vivafit.ai.handlers.ReactiveChatHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class ReactiveChatRouterConfiguration {

    @Bean
    public RouterFunction<ServerResponse> chatRoute(ReactiveChatHandler chatHandler) {
        return RouterFunctions.route()
                .POST("/api/chat/stream", chatHandler::streamChat)
                .DELETE("/api/chat/reset-history", chatHandler::resetHistory)
                .onError(Exception.class, (e, request) ->
                        ServerResponse.badRequest().bodyValue("Error: " + e.getMessage()))
                .build();
    }
}
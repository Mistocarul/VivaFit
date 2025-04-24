package com.vivafit.vivafit.ai.service;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AiChatService {
    private final ChatClient chatClient;
    private final ConversationHistoryService conversationHistoryService;

    public Flux<String> chatStream(String token, String prompt, String category, String foodDate, String foodName) {
        conversationHistoryService.addMessageToHistory(token, new UserMessage(prompt));
        List<Message> conversationHistory = conversationHistoryService.getConversationHistory(token);
        String fullPrompt = getPromptForCategory(prompt, category, foodDate, foodName);

        return chatClient.prompt()
                .system(fullPrompt)
                .messages(conversationHistory)
                .stream()
                .content()
                .map(content -> {
                    if (content != null && !content.isBlank()) {
                        conversationHistoryService.addMessageToHistory(token, new AssistantMessage(content));
                        return content;
                    }
                    return "";
                });
    }

    private String getPromptForCategory(String prompt, String category, String foodDate, String foodName) {
        if ("DISCUTIE".equalsIgnoreCase(category)) {
            return "Ești un asistent AI specializat în nutriție, fitness și alimentație. Răspunzi la întrebări scurt, clar și folosind informații actualizate și relevante din aceste domenii.\n"
                    + "Întrebarea utilizatorului este: \"" + prompt + "\".\n"
                    + "Te rog să NU repeți această întrebare în răspuns și să NU reformulezi instrucțiunile pe care ți le-am dat.\n"
                    + "Dacă întrebarea nu este legată de nutriție, fitness sau alimentație, răspunde cu exact acest text: \"Îmi pare rău, nu pot răspunde la această întrebare pentru că nu ține de nutriție, fitness sau alimentație.\"";
        } else if ("ANALIZA_MANCARE".equalsIgnoreCase(category)) {
            return "Ești un asistent AI specializat în nutriție, fitness și alimentație.\n"
                    + "Te rog să analizezi următorul preparat: \"" + foodName + "\", consumat pe data de " + foodDate + ".\n"
                    + "Pe baza informațiilor oferite în promptul următor: \"" + prompt + "\", efectuează următoarele:\n"
                    + "1. Estimează aproximativ valorile nutriționale: calorii, proteine, carbohidrați și grăsimi.\n"
                    + "2. Oferă o opinie despre cât de sănătos este preparatul.\n"
                    + "3. Sugerează o modalitate de îmbunătățire pentru a-l face mai echilibrat nutrițional.\n"
                    + "4. Acordă un scor de la 1 la 10 pentru calitatea nutrițională.\n"
                    + "Te rog să NU repeți detaliile din acest prompt și să NU reformulezi instrucțiunile. Răspunde direct, clar și structurat.";
        }
        return "Îmi pare rău, nu sunt instruit să răspund pe acest subiect. Te rog oferă un subiect legat de nutriție, fitness sau alimentație.";
    }
}
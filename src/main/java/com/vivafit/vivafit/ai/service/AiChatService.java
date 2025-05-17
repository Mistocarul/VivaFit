package com.vivafit.vivafit.ai.service;

import com.vivafit.vivafit.authentification.entities.User;
import com.vivafit.vivafit.manage_calories.dto.MealAnalizeAiDto;
import com.vivafit.vivafit.manage_calories.services.MealService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Service
public class AiChatService {
    @Autowired
    private ChatClient chatClient;
    @Autowired
    private ConversationHistoryService conversationHistoryService;
    @Autowired
    private MealService mealService;

    public Flux<String> chatStream(User user, String token, String prompt, String category, String mealType, LocalDate mealDate) {
        conversationHistoryService.addMessageToHistory(token, new UserMessage(prompt));
        List<Message> conversationHistory = conversationHistoryService.getConversationHistory(token);
        String fullPrompt = getPromptForCategory(user, prompt, category, mealType, mealDate);

        return Flux.create(sink -> {
            // Înregistrăm stream-ul activ pentru token
            conversationHistoryService.registerStream(token, sink);

            // Mesaj inițial către client
            sendSafe(sink, "Permite-mi câteva momente să îți răspund... ");

            chatClient.prompt()
                    .system(fullPrompt)
                    .messages(conversationHistory)
                    .stream()
                    .content()
                    .filter(Objects::nonNull)
                    .delayElements(Duration.ofMillis(300))
                    .doOnNext(content -> {
                        conversationHistoryService.addMessageToHistory(token, new AssistantMessage(content));
                        sendSafe(sink, content);
                    })
                    .doOnComplete(() -> completeSafe(sink))
                    .doOnError(e -> errorSafe(sink, e))
                    .subscribe();

        }, FluxSink.OverflowStrategy.LATEST);
    }

    private void sendSafe(FluxSink<String> sink, String message) {
        try {
            if (!sink.isCancelled()) {
                sink.next(message);
            }
        } catch (Exception e) {;
        }
    }

    private void completeSafe(FluxSink<String> sink) {
        try {
            if (!sink.isCancelled()) {
                sink.complete();
            }
        } catch (Exception e) {
        }
    }

    private void errorSafe(FluxSink<String> sink, Throwable e) {
        try {
            if (!sink.isCancelled()) {
                //sink.error(e);
                System.err.println("Error occurred: " + e.getMessage());
            }
        } catch (Exception ex) {
        }
    }


    private String getPromptForCategory(User user, String prompt, String category, String mealType, LocalDate mealDate) {
        System.out.println(prompt);
        if ("DISCUTIE".equalsIgnoreCase(category)) {
            return "Ești un asistent AI specializat în nutriție, fitness și alimentație. Răspunzi la întrebări scurt, clar și folosind informații actualizate și relevante din aceste domenii.\n"
                    + "Întrebarea utilizatorului este: \"" + prompt + "\".\n"
                    + "Te rog să NU repeți această întrebare în răspuns și să NU reformulezi instrucțiunile pe care ți le-am dat.\n"
                    + "Dacă întrebarea nu este legată de nutriție, fitness sau alimentație, răspunde cu exact acest text: \"Îmi pare rău, nu pot răspunde la această întrebare pentru că nu ține de nutriție, fitness sau alimentație.\"";
        } else if ("AnalizaMancare".equalsIgnoreCase(category)) {
            MealAnalizeAiDto mealAnalizeAiDto = mealService.getMealAnalysisForAI(mealDate, mealType, user.getId());
            System.out.println(mealAnalizeAiDto.toString());
            if (mealAnalizeAiDto == null) {
                return "Ești un asistent AI specializat în nutriție, fitness și alimentație și te rog să scrii următoarea propoziție fara alte detalii. " +
                        "Îmi pare rău, nu am găsit informații despre masa specificată. Te rog să verifici data și tipul mesei.";
            }
            return "Ești un asistent AI specializat în nutriție și alimentație.\n"
                    + "Îți voi furniza o masă de tip \"" + mealAnalizeAiDto.getMealType() + "\" consumată pe data de " + mealDate + ".\n"
                    + "Lista alimentelor consumate la această masă, împreună cu valorile lor nutriționale, este următoarea:\n"
                    + mealAnalizeAiDto.toString() + "\n"
                    + "Te rog să efectuezi următoarele:\n"
                    + "1. Calculează totalul pentru calorii, proteine, carbohidrați și grăsimi pentru întreaga masă.\n"
                    + "2. Evaluează cât de sănătoasă este această masă, ținând cont de valorile nutriționale totale.\n"
                    + "3. Propune o modalitate de îmbunătățire pentru a echilibra mai bine masa din punct de vedere nutrițional.\n"
                    + "4. Acordă un scor de la 1 la 10 pentru calitatea nutrițională a mesei.\n"
                    + "Răspunde clar, structurat și direct, fără a repeta detaliile din acest prompt.";
        }
        return "Îmi pare rău, nu sunt instruit să răspund pe acest subiect. Te rog oferă un subiect legat de nutriție, fitness sau alimentație.";
    }
}
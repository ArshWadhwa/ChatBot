package chatBot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.List;
import java.util.Map;

@Service
public class AIService {
    private final WebClient webClient;

    @Value("${openrouter.api.key}")
    private String openRouterApiKey;

    @Autowired
    public AIService(@Value("${openrouter.api.key}") String apiKey) {
        this.openRouterApiKey = apiKey;
        this.webClient = WebClient.builder()
                .baseUrl("https://openrouter.ai/api/v1/chat/completions") // ✅ Correct endpoint
                .defaultHeader("Authorization", "Bearer " + this.openRouterApiKey)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    @SuppressWarnings("unchecked")
    public Mono<String> getAIResponse(String userMessage) {
        return webClient.post()
                .bodyValue(Map.of(
                        "model", "deepseek/deepseek-r1:free",
                        "messages", List.of(
                                Map.of("role", "system", "content", "You are a helpful AI chatbot."),
                                Map.of("role", "user", "content", userMessage)
                        )
                ))
                .retrieve()
                .bodyToMono(Map.class)
                .map(response -> {
                    List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
                    if (choices != null && !choices.isEmpty()) {
                        Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
                        if (message != null && message.containsKey("content")) {
                            return (String) message.get("content");  // ✅ Extract AI response
                        }
                    }
                    return "⚠️ AI response is empty. Please try again.";
                })
                .retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(5))) // ✅ Retry if API fails
                .onErrorResume(e -> {
                    e.printStackTrace(); // ✅ Log exact error
                    return Mono.just("⚠️ AI is currently overloaded. Error: " + e.getMessage());
                });
    }
}

package chatBot.controller;


import chatBot.AIService;
import chatBot.entity.ChatMessage;
import chatBot.repository.ChatMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;
@RestController // ✅ Add this
@CrossOrigin(origins = "http://127.0.0.1:5501")

public class ChatController {
    @Autowired
private ChatMessageRepository chatMessageRepository;

    @Autowired
    private AIService aiService;


    @PostMapping("/send")
    public Mono<ChatMessage> sendMessage(@RequestBody ChatMessage chatMessage){
        return aiService.getAIResponse(chatMessage.getMessage())
                .map(aiResponse ->{
                    chatMessage.setReceiver("Ai");
                    chatMessage.setMessage(aiResponse);
                    return chatMessageRepository.save(chatMessage);
                        });
    }

    @GetMapping("/messages")
    public List<ChatMessage> getMessages(){
        return chatMessageRepository.findAll();
    }

    @GetMapping("/messages/{sender}")
    public List<ChatMessage> getMessagesBySender(@PathVariable String sender) {
        return chatMessageRepository.findBySender(sender);
    }



}

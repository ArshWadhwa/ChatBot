package chatBot.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Setter;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "chat_messages")
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Column(nullable = false) // ✅ Ensures it's not null
    private String sender;

    @Setter
    @Column(nullable = false) // ✅ Ensures it's not null
    private String receiver;  // ✅ Add this

    @Setter
    @Column(nullable = false) // ✅ Ensures it's not null
    private String message;

    @Column(name = "sent_at", nullable = false)
    private LocalDateTime sentAt;

    public ChatMessage() {
        this.sentAt = LocalDateTime.now();
    }

    public ChatMessage(String sender, String receiver, String message) {  // ✅ Constructor updated
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.sentAt = LocalDateTime.now();
    }
}

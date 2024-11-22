package io.chatguard.chatguard.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "log_entries")
public class LogEntryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "chat_id", nullable = false)
    private Long chatId;

    @NotNull
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "username", nullable = false)
    private String username;

    @NotNull
    @Size(max = 1000)
    @Column(name = "message_text", columnDefinition = "TEXT")
    private String messageText;

    @Column(name = "message_type")
    private String messageType;

    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "removal_reason", nullable = false)
    private String removalReason;

    @NotNull
    @Past
    @Column(name = "removal_time", nullable = false)

    private LocalDateTime removalTime;

    public LogEntryEntity() {}

    public LogEntryEntity(Long chatId, Long userId, String username, String messageText,
                          String messageType, String removalReason, LocalDateTime removalTime) {
        this.chatId = chatId;
        this.userId = userId;
        this.username = username;
        this.messageText = messageText;
        this.messageType = messageType;
        this.removalReason = removalReason;
        this.removalTime = removalTime;
    }
}

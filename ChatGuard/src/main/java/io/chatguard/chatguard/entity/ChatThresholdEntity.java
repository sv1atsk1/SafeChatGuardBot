package io.chatguard.chatguard.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "chat_thresholds")
public class ChatThresholdEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "chat_id", nullable = false, unique = true)
    private Long chatId;

    @Min(value = 0, message = "Threshold must be greater than or equal to 0")
    @Max(value = 1, message = "Threshold must be less than or equal to 1")
    @Column(name = "threshold", nullable = false)
    private double threshold;

    public ChatThresholdEntity() {}

    public ChatThresholdEntity(Long chatId, double threshold) {
        this.chatId = chatId;
        this.threshold = threshold;
    }
}

package io.chatguard.chatguard.service;

import io.chatguard.chatguard.entity.ChatThresholdEntity;
import io.chatguard.chatguard.repository.ChatThresholdRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class ThresholdService {

    private final ChatThresholdRepository thresholdRepository;

    public ThresholdService(ChatThresholdRepository thresholdRepository) {
        this.thresholdRepository = thresholdRepository;
    }

    public void setChatThreshold(Long chatId, double threshold) {
        ChatThresholdEntity chatThresholdEntity = thresholdRepository.findByChatId(chatId)
                .orElse(new ChatThresholdEntity(chatId, threshold));
        chatThresholdEntity.setThreshold(threshold);
        thresholdRepository.save(chatThresholdEntity);
    }

    public double getChatThreshold(Long chatId) {
        return thresholdRepository.findByChatId(chatId)
                .map(ChatThresholdEntity::getThreshold)
                .orElse(0.5);
    }

    @Transactional
    public String removeChatThreshold(Long chatId) {
        Optional<ChatThresholdEntity> chatThreshold = thresholdRepository.findByChatId(chatId);
        if (chatThreshold.isPresent()) {
            thresholdRepository.deleteByChatId(chatId);
            return "Порог удален.";
        } else {
            return "Порог не установлен для этого чата.";
        }
    }
}
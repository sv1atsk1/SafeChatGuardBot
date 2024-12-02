package io.chatguard.chatguard.service;

import io.chatguard.chatguard.entity.ChatThresholdEntity;
import io.chatguard.chatguard.repository.ChatThresholdRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

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
    public void removeChatThreshold(Long chatId) {
        thresholdRepository.findByChatId(chatId).ifPresent(chatThreshold ->
            thresholdRepository.deleteByChatId(chatThreshold.getChatId()));
    }
}
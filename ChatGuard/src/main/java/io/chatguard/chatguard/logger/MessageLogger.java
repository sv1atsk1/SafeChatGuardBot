package io.chatguard.chatguard.logger;

import io.chatguard.chatguard.entity.LogEntryEntity;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class MessageLogger {

    private final EntityManager entityManager;

    public MessageLogger(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Transactional
    public void logTextMessage(Long chatId, Long userId, String username, String messageText,
                               String messageType, String removalReason) {

        LogEntryEntity logEntryEntity = new LogEntryEntity(
                chatId, userId, username, messageText, messageType, removalReason, LocalDateTime.now());

        entityManager.persist(logEntryEntity);
    }

    @Transactional
    public void logImageMessage(Long chatId, Long userId, String username, byte[] image, String messageText,
                                String messageType, String removalReason) {

        LogEntryEntity logEntryEntity = new LogEntryEntity(
                chatId, userId, username, image, messageText, messageType, removalReason, LocalDateTime.now());

        entityManager.persist(logEntryEntity);
    }
}
package io.chatguard.chatguard.logger;

import io.chatguard.chatguard.entity.LogEntryEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

@Component
public class MessageLogger {

    private final EntityManager entityManager;

    public MessageLogger(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public void logMessage(Long chatId, Long userId, String username, String messageText,
                           String messageType, String removalReason) {
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            LogEntryEntity logEntryEntity = new LogEntryEntity(
                    chatId, userId, username, messageText, messageType, removalReason, LocalDateTime.now()
            );
            entityManager.persist(logEntryEntity);
            transaction.commit();
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw e;
        }
    }
}
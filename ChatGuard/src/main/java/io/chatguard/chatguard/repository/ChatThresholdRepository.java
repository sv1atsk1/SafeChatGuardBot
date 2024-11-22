package io.chatguard.chatguard.repository;

import io.chatguard.chatguard.entity.ChatThresholdEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ChatThresholdRepository extends JpaRepository<ChatThresholdEntity, Long> {

    Optional<ChatThresholdEntity> findByChatId(Long chatId);

    void deleteByChatId(Long chatId);
}

package io.chatguard.chatguard.service;

import io.chatguard.chatguard.entity.BlacklistedUserEntity;
import io.chatguard.chatguard.repository.BlacklistRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class BlacklistService {

    private final BlacklistRepository blacklistRepository;

    public BlacklistService(BlacklistRepository blacklistRepository) {
        this.blacklistRepository = blacklistRepository;
    }

    public void addToBlacklist(String username) {
        if (blacklistRepository.findByUsername(username).isEmpty()) {
            blacklistRepository.save(new BlacklistedUserEntity(username));
        }
    }

    @Transactional
    public void removeFromBlacklist(String username) {
        blacklistRepository.deleteByUsername(username);
    }
    public boolean isUserBlacklisted(String username) {
        return blacklistRepository.findByUsername(username).isPresent();
    }
}
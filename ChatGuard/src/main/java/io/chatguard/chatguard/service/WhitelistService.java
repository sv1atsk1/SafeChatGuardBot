package io.chatguard.chatguard.service;

import io.chatguard.chatguard.entity.WhitelistedUserEntity;
import io.chatguard.chatguard.repository.WhitelistRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class WhitelistService {

    private final WhitelistRepository whitelistRepository;

    public WhitelistService(WhitelistRepository whitelistRepository) {
        this.whitelistRepository = whitelistRepository;
    }

    public void addToWhitelist(String username) {
        if (whitelistRepository.findByUsername(username).isEmpty()) {
            whitelistRepository.save(new WhitelistedUserEntity(username));
        }
    }

    @Transactional
    public void removeFromWhitelist(String username) {
        whitelistRepository.deleteByUsername(username);
    }
    public boolean isUserWhitelisted(String username) {
        return whitelistRepository.findByUsername(username).isPresent();
    }
}
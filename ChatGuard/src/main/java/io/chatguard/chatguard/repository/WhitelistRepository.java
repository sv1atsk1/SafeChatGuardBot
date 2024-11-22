package io.chatguard.chatguard.repository;

import io.chatguard.chatguard.entity.WhitelistedUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface WhitelistRepository extends JpaRepository<WhitelistedUserEntity, Long> {

    Optional<WhitelistedUserEntity> findByUsername(String username);

    void deleteByUsername(String username);
}
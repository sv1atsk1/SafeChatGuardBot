package io.chatguard.chatguard.repository;

import io.chatguard.chatguard.entity.BlacklistedUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface BlacklistRepository extends JpaRepository<BlacklistedUserEntity, Long> {

    Optional<BlacklistedUserEntity> findByUsername(String username);

    void deleteByUsername(String username);
}
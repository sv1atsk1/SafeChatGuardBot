package io.chatguard.chatguard.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "blacklist")
public class BlacklistedUserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Size(max = 255)
    @Column(name = "username", nullable = false, unique = true)
    private String username;

    public BlacklistedUserEntity() {}

    public BlacklistedUserEntity(String username) {
        this.username = username;
    }
}

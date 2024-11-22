package io.chatguard.chatguard.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "whitelist")
public class WhitelistedUserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Size(max = 255)
    @Column(name = "username", nullable = false, unique = true)
    private String username;

    public WhitelistedUserEntity() {}

    public WhitelistedUserEntity(String username) {
        this.username = username;
    }
}

package com.learning.springSecurity.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
@Entity
@Table(name = "confirmation_token")
public class TokenEntity extends BaseEntity{
    @Id
    @SequenceGenerator(name = "confirmation_token_sequence",
            sequenceName = "confirmation_token_sequence",
            initialValue = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;
    @Column(name = "token", nullable = false)
    private String token;
    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;
    @Column(name = "confirmed_at")
    private LocalDateTime confirmedAt;
    @Enumerated(EnumType.STRING)
    @Column(name = "token_type")
    private TokenType tokenType;

    public TokenEntity(UserEntity user, String token, LocalDateTime expiresAt, TokenType tokenType) {
        this.user = user;
        this.token = token;
        this.expiresAt = expiresAt;
        this.tokenType = tokenType;
    }
}

package org.registrationservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entity class for a confirmation token.
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "confirmation_tokens")
public class ConfirmationToken {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    /**
     * Conformation token is a UUID. The confirmation token must be unique and not null.
     */
    @Column(nullable = false,unique = true)
    private UUID token;

    /**
     * The time and date that the confirmation token was created. This field cannot be null.
     */
    @Column(nullable = false)
    private LocalDateTime createdAt;

    /**
     * The time and date when the confirmation token will expire (the amount of time in which the user
     * can confirm is account is the difference between this field and createAt field).
     * This field cannot me null.
     */
    @Column(nullable = false)
    private LocalDateTime expiresAt;

    /**
     * The time and date when the user confirmed is account. Initially, this value is null but if the
     * user confirms his account, the value will be updated.
     */
    private LocalDateTime confirmedAt;
}
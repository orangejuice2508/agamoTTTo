package de.gruppe2.agamoTTTo.domain.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "password_reset_token")
public class PasswordResetToken {

    private static final int EXPIRATION_IN_MINUTES = 60 * 24;

    @Id
    @GeneratedValue
    @Column(name = "password_reset_token_id")
    private Long id;

    @NotNull
    @Column(name = "token")
    private String token;

    @OneToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "user_id")
    private User user;

    @NotNull
    @Column(name = "expiry_date")
    private LocalDateTime expiryDate;

    public PasswordResetToken(@NotNull String token, User user) {
        this.token = token;
        this.user = user;
        this.expiryDate = LocalDateTime.now().plusMinutes(EXPIRATION_IN_MINUTES);
    }
}

package de.gruppe2.agamoTTTo.service;

import de.gruppe2.agamoTTTo.domain.entity.PasswordResetToken;
import de.gruppe2.agamoTTTo.repository.PasswordResetTokenRepository;
import de.gruppe2.agamoTTTo.security.CustomSecurityUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;

@Service
public class SecurityService {

    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Autowired
    public SecurityService(PasswordResetTokenRepository passwordResetTokenRepository) {
        this.passwordResetTokenRepository = passwordResetTokenRepository;
    }

    /**
     * This method checks whether a PasswordResetToken is valid:
     * It must belong to the user identified by the userId
     * AND it must not be expired.
     *
     * @param userId the user id which should be checked
     * @param token the token which should be checked
     * @return true if the PasswordResetToken is valid, false if it is not valid
     */
    public Boolean isPasswordResetTokenValid(Long userId, String token) {

        // Get the full PasswordResetToken by the token
        PasswordResetToken passwordResetToken = passwordResetTokenRepository.findByToken(token);

        // If no such passwordResetToken could be found or the users do not match, false is returned.
        if ((passwordResetToken == null) || !passwordResetToken.getUser().getId().equals(userId)) {
            return false;
        }

        // If the passwordResetToken has already expired, false will be returned
        LocalDateTime now = LocalDateTime.now();
        if (now.isAfter(passwordResetToken.getExpiryDate())) {
            return false;
        }

        // If the passwordResetToken is valid, the user gets a temporary authentication for updating the password.
        CustomSecurityUser user = new CustomSecurityUser(passwordResetToken.getUser());
        Authentication auth = new UsernamePasswordAuthenticationToken(user, null,
                Collections.singletonList(new SimpleGrantedAuthority("UPDATE_PASSWORD_PRIVILEGE")));
        SecurityContextHolder.getContext().setAuthentication(auth);

        // The passwordResetToken is only valid once. This is why it is immediately deleted after verifying its validity.
        passwordResetTokenRepository.delete(passwordResetToken);

        return true;
    }
}

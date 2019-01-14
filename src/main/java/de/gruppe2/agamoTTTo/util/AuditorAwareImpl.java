package de.gruppe2.agamoTTTo.util;

import de.gruppe2.agamoTTTo.security.CustomSecurityUser;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Since we use @CreatedBy and @LastModifiedBy, those annotations need to get
 * the currently logged in user, which modified or created an entity.
 */

@Component(value = "auditorAware")
public class AuditorAwareImpl implements AuditorAware<CustomSecurityUser> {

    /**
     * This method uses the SecurityContextHolder, which is provided by Spring Security,
     * to retrieve the current user, which is logged in. If no such authentication is found
     * the auditor is null and an empty optional is returned. Else:
     *
     * @return auditor the user which created or modified an entity
     */
    public Optional<CustomSecurityUser> getCurrentAuditor() {
        Optional<CustomSecurityUser> auditor;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            auditor = Optional.empty();
        }
        else{
            auditor = Optional.of((CustomSecurityUser) authentication.getPrincipal());
        }

        return auditor;
    }
}

package de.gruppe2.agamoTTTo.util;

import de.gruppe2.agamoTTTo.security.CustomSecurityUser;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component(value = "auditorAware")
public class AuditorAwareImpl implements AuditorAware<CustomSecurityUser> {

    public Optional<CustomSecurityUser> getCurrentAuditor() {
        Optional<CustomSecurityUser> auditor;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            auditor = Optional.empty();
        }
        else{
            auditor = Optional.of((CustomSecurityUser) authentication.getPrincipal());
        }

        return auditor;
    }
}

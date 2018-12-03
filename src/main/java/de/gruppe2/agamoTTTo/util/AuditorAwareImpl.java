package de.gruppe2.agamoTTTo.util;

import de.gruppe2.agamoTTTo.entity.User;
import de.gruppe2.agamoTTTo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component(value = "auditorAware")
public class AuditorAwareImpl implements AuditorAware<User> {

    private UserRepository userRepository;

    @Autowired
    public AuditorAwareImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<User> getCurrentAuditor() {
        Optional<User> auditor;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            auditor = Optional.empty();
        }
        else {
            UserDetails loggedInUser = (UserDetails) authentication.getPrincipal();
            auditor = Optional.of(userRepository.findByEmail(loggedInUser.getUsername()));
        }

        return auditor;
    }
}

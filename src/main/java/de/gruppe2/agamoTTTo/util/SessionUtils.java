package de.gruppe2.agamoTTTo.util;

import de.gruppe2.agamoTTTo.domain.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class SessionUtils {

    private SessionRegistry sessionRegistry;

    @Autowired
    public SessionUtils(SessionRegistry sessionRegistry) {
        this.sessionRegistry = sessionRegistry;
    }

    public void expireUserSessions(String email) {
        for (Object principal : sessionRegistry.getAllPrincipals()) {
            if (principal instanceof User) {
                UserDetails userDetails = (UserDetails) principal;
                if (userDetails.getUsername().equals(email)) {
                    for (SessionInformation information : sessionRegistry.getAllSessions(userDetails, true)) {
                        information.expireNow();
                    }
                }
            }
        }
    }
}

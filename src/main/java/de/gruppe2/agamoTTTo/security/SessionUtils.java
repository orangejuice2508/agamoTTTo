package de.gruppe2.agamoTTTo.security;

import de.gruppe2.agamoTTTo.domain.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Component;

/**
 * This class is primarily used for expiring user sessions
 */
@Component
public class SessionUtils {

    private SessionRegistry sessionRegistry;

    @Autowired
    public SessionUtils(SessionRegistry sessionRegistry) {
        this.sessionRegistry = sessionRegistry;
    }

    /**
     * This method asks for all user sessions in the registry of the server and
     * if the email is equal to the email stored in a session, this specific session
     * gets expired.
     *
     * @param email the mail of a user of which all session should get expired.
     */
    public void expireUserSessions(String email) {
        // For each object in the session registry.
        for (Object principal : sessionRegistry.getAllPrincipals()) {
            // ... If it is a User object...
            if (principal instanceof User) {
                // ... then cast it to a User object.
                User user = (User) principal;
                // If the emails are equal...
                if (user.getEmail().equals(email)) {
                    // ... then get all sessions of this User ...
                    for (SessionInformation information : sessionRegistry.getAllSessions(user, true)) {
                        // ...  and expire them.
                        information.expireNow();
                    }
                }
            }
        }
    }
}

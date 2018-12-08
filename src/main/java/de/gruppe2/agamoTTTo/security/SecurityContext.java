package de.gruppe2.agamoTTTo.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Our "custom" SecurityContext.
 */

public final class SecurityContext {

    /**
     * Make it impossible to instantiate this class.
     */
    private SecurityContext() {
    }

    /**
     * This method uses the static getAuthentication method from below to retrieve the current user,
     * which is logged in. If no such authentication is found, the user is null and the null value is returned.
     * Else:
     *
     * @return loggedInUser the user which is currently logged in
     */
    public static CustomSecurityUser getAuthenticationUser(){
        CustomSecurityUser loggedInUser = null;
        Authentication authentication = getAuthentication();

        if (authentication != null) {
            loggedInUser = (CustomSecurityUser) authentication.getPrincipal();
        }

        return loggedInUser;
    }

    /**
     * This method uses the SecurityContextHolder, which is provided by Spring Security,
     * to retrieve the current authentication, i.e. the authentication token of any user.
     * This can be null, if the user of the application is not logged in.
     *
     * @return authentication the token of a logged in user
     */
    private static Authentication getAuthentication(){
        return SecurityContextHolder.getContext().getAuthentication();
    }
}

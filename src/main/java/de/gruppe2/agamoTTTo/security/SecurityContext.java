package de.gruppe2.agamoTTTo.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class SecurityContext {

    private SecurityContext() {
    }

    public static CustomSecurityUser getAuthenticationUser(){
        CustomSecurityUser loggedInUser = null;
        Authentication authentication = getAuthentication();

        if (authentication != null) {
            loggedInUser = (CustomSecurityUser) authentication.getPrincipal();
        }

        return loggedInUser;
    }

    private static Authentication getAuthentication(){
        return SecurityContextHolder.getContext().getAuthentication();
    }
}

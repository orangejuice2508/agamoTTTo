package de.gruppe2.agamoTTTo.security;

/**
 * This class helps us the shorten the permission annotations.
 * So instead of @PreAuthorize(hasRole('ROLE_ADMINISTRATOR'))
 * we can simply write: @PreAuthorize(Permission.ADMINISTRATOR).
 */
public class Permission {
    public static final String ADMINISTRATOR = "hasRole('ROLE_ADMINISTRATOR')";
    public static final String VORGESETZTER = "hasRole('ROLE_VORGESETZTER')";
    public static final String MITARBEITER = "hasRole('ROLE_MITARBEITER')";
    public static final String IS_AUTHENTICATED = "isAuthenticated()";
}

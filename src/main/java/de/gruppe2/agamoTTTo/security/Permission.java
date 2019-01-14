package de.gruppe2.agamoTTTo.security;

/**
 * This class helps us the shorten the permission annotations.
 * So instead of @PreAuthorize(hasAnyRole('ROLE_ADMINISTRATOR'))
 * we can simply write: @PreAuthorize(Permission.ADMINISTRATOR).
 */
public class Permission {
    public static final String ADMINISTRATOR = "hasAnyRole('ROLE_ADMINISTRATOR')";
    public static final String VORGESETZTER = "hasAnyRole('ROLE_VORGESETZTER')";
    public static final String MITARBEITER = "hasAnyRole('ROLE_MITARBEITER')";
    public static final String IS_AUTHENTICATED = "isAuthenticated()";
}

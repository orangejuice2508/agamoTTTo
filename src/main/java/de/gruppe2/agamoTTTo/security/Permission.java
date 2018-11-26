package de.gruppe2.agamoTTTo.security;

public class Permission {
    public static final String ADMINISTRATOR = "hasAnyRole('ROLE_ADMINISTRATOR')";
    public static final String VORGESETZTER = "hasAnyRole('ROLE_VORGESETZTER')";
    public static final String MITARBEITER = "hasAnyRole('ROLE_MITARBEITER')";
}

package de.gruppe2.agamoTTTo.security;

public class SecurityRole {
    public static final String ADMINISTRATOR = "ROLE_ADMINISTRATOR";
    public static final String VORGESETZTER = "ROLE_VORGESETZTER";
    public static final String MITARBEITER = "ROLE_MITARBEITER";

    public static String getRoleHierarchyStringRepresentation(){
        return  SecurityRole.ADMINISTRATOR + " > "
                + SecurityRole.VORGESETZTER + " > "
                + SecurityRole.MITARBEITER;
    }
}

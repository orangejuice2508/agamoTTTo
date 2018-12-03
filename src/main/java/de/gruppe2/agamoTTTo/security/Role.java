package de.gruppe2.agamoTTTo.security;

public class Role {
    public static final String ADMINISTRATOR = "ROLE_ADMINISTRATOR";
    public static final String VORGESETZTER = "ROLE_VORGESETZTER";
    public static final String MITARBEITER = "ROLE_MITARBEITER";

    public static String getRoleHierarchyStringRepresentation(){
        return  Role.ADMINISTRATOR + " > "
                + Role.VORGESETZTER + " > "
                + Role.MITARBEITER;
    }
}

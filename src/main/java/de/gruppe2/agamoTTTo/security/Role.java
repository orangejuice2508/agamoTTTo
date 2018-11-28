package de.gruppe2.agamoTTTo.security;

public class Role {
    private static final String ADMINISTRATOR = "ROLE_ADMINISTRATOR";
    private static final String VORGESETZTER = "ROLE_VORGESETZTER";
    private static final String MITARBEITER = "ROLE_MITARBEITER";

    public static String getRoleHierarchyStringRepresentation(){
        return  Role.ADMINISTRATOR + " > "
                + Role.VORGESETZTER + " > "
                + Role.MITARBEITER;
    }
}

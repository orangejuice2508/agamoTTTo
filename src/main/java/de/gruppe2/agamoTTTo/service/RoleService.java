package de.gruppe2.agamoTTTo.service;

import de.gruppe2.agamoTTTo.domain.entity.Role;
import de.gruppe2.agamoTTTo.domain.entity.User;
import de.gruppe2.agamoTTTo.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class RoleService {

    private RoleRepository roleRepository;

    @Autowired
    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    /**
     * This method uses the roleRepository to find all roles from the database which are possible for the user.
     * "Possible" indicates that a user can only be promoted but never degraded.
     *
     * @param user the user whose possible roles should be found
     * @return all roles in the database
     */
    public Set<Role> findPossibleRoles(User user) {
        Set<Role> possibleRoles = new HashSet<>(roleRepository.findAll());

        if (!user.getRole().getRoleName().equals(de.gruppe2.agamoTTTo.security.Role.MITARBEITER)) {
            possibleRoles.removeIf(role -> role.getRoleName().equals(de.gruppe2.agamoTTTo.security.Role.MITARBEITER));
        }
        if (user.getRole().getRoleName().equals(de.gruppe2.agamoTTTo.security.Role.ADMINISTRATOR)) {
            possibleRoles.removeIf(role -> role.getRoleName().equals(de.gruppe2.agamoTTTo.security.Role.VORGESETZTER));
        }

        return possibleRoles;
    }
}

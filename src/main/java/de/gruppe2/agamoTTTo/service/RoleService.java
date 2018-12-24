package de.gruppe2.agamoTTTo.service;

import de.gruppe2.agamoTTTo.domain.entity.Role;
import de.gruppe2.agamoTTTo.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleService {

    private RoleRepository roleRepository;

    @Autowired
    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    /**
     * This method uses the roleRepository to find all roles from the database.
     *
     * @return all roles in the database
     */
    public List<Role> findAllRoles() {
        return roleRepository.findAll();
    }
}

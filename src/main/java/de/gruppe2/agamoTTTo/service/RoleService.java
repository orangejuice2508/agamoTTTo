package de.gruppe2.agamoTTTo.service;

import de.gruppe2.agamoTTTo.entity.Role;
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

    public String getRoleHierarchyStringRepresentation(){
        List<Role> roles = roleRepository.findAllByOrderById();
        StringBuilder stringBuilder = new StringBuilder();

        for (Role role : roles){
            stringBuilder.append(role.getRoleName());
            if(roles.size() != role.getId()) {
                stringBuilder.append(" > ");
            }
        }

        return stringBuilder.toString();
    }
}

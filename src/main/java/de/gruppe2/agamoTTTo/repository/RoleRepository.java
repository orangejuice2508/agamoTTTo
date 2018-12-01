package de.gruppe2.agamoTTTo.repository;

import de.gruppe2.agamoTTTo.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByRoleName(String roleName);
}

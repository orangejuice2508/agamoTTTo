package de.gruppe2.agamoTTTo.repository;

import de.gruppe2.agamoTTTo.domain.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * This repository is used for crud operations (create/read/update/delete) on the table "role" in the database.
 * Note: The queries are constructed automatically by Spring JPA based on method naming conventions.
 */
public interface RoleRepository extends JpaRepository<Role, Long> {
}

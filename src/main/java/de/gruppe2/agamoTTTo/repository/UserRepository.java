package de.gruppe2.agamoTTTo.repository;

import de.gruppe2.agamoTTTo.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * This repository is used for crud operations (create/read/update/delete) on the table "agamottto_user" in the database.
 * Note: The queries are constructed automatically by Spring JPA based on method naming conventions.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);

    @Query("select distinct u from User u " +
            "where u.firstName like %:searchTerm% or u.lastName like %:searchTerm% or u.email like %:searchTerm% " +
            "order by u.lastName asc, u.firstName asc, u.email asc")
    List<User> searchForUserByFirstNameOrLastNameOrEmail(@Param("searchTerm") String searchTerm);

    List<User> findAllByOrderByLastNameAscFirstNameAscEmailAsc();
}

package de.gruppe2.agamoTTTo.repository;

import de.gruppe2.agamoTTTo.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);

    @Query("select distinct u from User u where u.firstName like %:searchTerm% or u.lastName like %:searchTerm% or u.email like %:searchTerm%")
    Set<User> searchForUserByFirstNameOrLastNameOrEmail(@Param("searchTerm") String searchTerm);
}

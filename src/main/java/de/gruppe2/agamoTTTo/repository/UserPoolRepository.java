package de.gruppe2.agamoTTTo.repository;

import de.gruppe2.agamoTTTo.domain.entity.Pool;
import de.gruppe2.agamoTTTo.domain.entity.User;
import de.gruppe2.agamoTTTo.domain.entity.UserPool;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * This repository is used for crud operations (create/read/update/delete) on the table "user_pool" in the database.
 * Note: The queries are constructed automatically by Spring JPA based on method naming conventions.
 */
@Repository
public interface UserPoolRepository extends JpaRepository<UserPool, Long> {
    List<UserPool> findAllByUser(User user);

    List<UserPool> findAllByPool(Pool pool);

    Optional<UserPool> findByUserAndPoolAndIsActiveIsFalse(User user, Pool pool);
}

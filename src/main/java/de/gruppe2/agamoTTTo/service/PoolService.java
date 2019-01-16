package de.gruppe2.agamoTTTo.service;

import de.gruppe2.agamoTTTo.domain.entity.Pool;
import de.gruppe2.agamoTTTo.domain.entity.User;
import de.gruppe2.agamoTTTo.domain.entity.UserPool;
import de.gruppe2.agamoTTTo.repository.PoolRepository;
import de.gruppe2.agamoTTTo.repository.UserPoolRepository;
import de.gruppe2.agamoTTTo.security.Permission;
import de.gruppe2.agamoTTTo.security.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service which is used for dealing with the pools("Arbeitsbereiche") of our application.
 */
@Service
public class PoolService {

    private PoolRepository poolRepository;

    private UserPoolRepository userPoolRepository;

    @Autowired
    public PoolService(PoolRepository poolRepository, UserPoolRepository userPoolRepository) {
        this.poolRepository = poolRepository;
        this.userPoolRepository = userPoolRepository;
    }

    /**
     * This method uses the poolRepository to try to add a pool to the database.
     * Furthermore the userPoolRepository is used to assign the owner to the newly created pool.
     *
     * @param pool the pool as obtained from the controller
     */
    @PreAuthorize(Permission.VORGESETZTER)
    public void addPool(Pool pool){
        poolRepository.save(pool);
        userPoolRepository.save(new UserPool(pool.getOwner(), pool));
    }

    /**
     * This method uses the UserPoolRepository to add a user to a pool.
     *
     * @param userPool the entity which combines a user and a pool
     */
    @PreAuthorize(Permission.VORGESETZTER)
    public void addUserToPool(UserPool userPool) {
        userPoolRepository.save(userPool);
    }

    /**
     * This method uses the userRepository to find all pools which the logged in user is part of.
     *
     * @param user the user whose pools should be found
     * @param findAllExistingPools is only true on sites where the admin should see all pools (e.g. log/pool overview)
     * @return pools which the logged in user is part of
     */
    @PreAuthorize(Permission.MITARBEITER)
    public List<Pool> findAllPoolsOfUser(User user, Boolean findAllExistingPools) {
        // If the user is an admin and all pools should found, then return all existing pools
        if (user.getRole().getRoleName().equals(Role.ADMINISTRATOR) && findAllExistingPools) {
            return poolRepository.findAllByOrderByNameAsc();
        }

        return userPoolRepository.findAllByUser(user)
                .stream()
                .map(UserPool::getPool)
                .sorted(Comparator.comparing(Pool::getName))
                .collect(Collectors.toList());
    }

    /**
     * This method uses the poolRepository to find a certain pool from the database.
     * If no pool was found, an empty optional object is returned.
     *
     * @param id the id of the pool, which should be found
     * @return the optional pool
     */
    @PreAuthorize(Permission.VORGESETZTER)
    public Optional<Pool> findPoolById(Long id){
        return poolRepository.findById(id);
    }

    /**
     * This method checks if the specified user is an ACTIVE member in the specified pool.
     *
     * @param user the user whose assignments should be checked
     * @param pool the pool which should be checked for ACTIVE assignments of the user
     * @return true, if user is an ACTIVE member of the pool, false if not.
     */
    @PreAuthorize(Permission.VORGESETZTER)
    public Boolean isUserInPool(User user, Pool pool) {

        return !pool.getUserPools()
                .stream()
                .filter(userPool -> userPool.getUser().getId().equals(user.getId()))
                .filter(UserPool::getIsActive)
                .collect(Collectors.toSet())
                .isEmpty();
    }

    /**
     * This method uses the poolRepository to try to update to the database.
     *
     * @param updatedPool the updated pool as obtained from the controller
     */
    @PreAuthorize(Permission.VORGESETZTER)
    public void updatePool(Pool updatedPool) {
        // Use the getOne method, so that no more DB fetch has to be executed
        Pool poolToUpdate = poolRepository.getOne(updatedPool.getId());

        poolToUpdate.setName(updatedPool.getName());
        poolRepository.save(poolToUpdate);
    }
}
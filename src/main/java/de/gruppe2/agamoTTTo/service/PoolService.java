package de.gruppe2.agamoTTTo.service;

import de.gruppe2.agamoTTTo.domain.entity.Pool;
import de.gruppe2.agamoTTTo.domain.entity.User;
import de.gruppe2.agamoTTTo.repository.PoolRepository;
import de.gruppe2.agamoTTTo.repository.UserRepository;
import de.gruppe2.agamoTTTo.security.Permission;
import de.gruppe2.agamoTTTo.security.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Service which is used for dealing with the pools("Arbeitsbereiche") of our application.
 */
@Service
public class PoolService {

    private PoolRepository poolRepository;

    private UserRepository userRepository;

    @Autowired
    public PoolService(PoolRepository poolRepository, UserRepository userRepository) {
        this.poolRepository = poolRepository;
        this.userRepository = userRepository;
    }

    /**
     * This method uses the poolRepository to try to add a pool to the database.
     *
     * @param pool the pool as obtained from the controller
     */
    @PreAuthorize(Permission.VORGESETZTER)
    public void addPool(Pool pool){
        poolRepository.save(pool);
    }

    /**
     * This method uses the userRepository to find all pools which the logged in user is part of.
     *
     * @param user the user whose pools should be found
     * @param findAllExistingPools is only true on sites where the admin should see all pools (e.g. log/pool overview)
     * @return pools which the logged in user is part of
     */
    @PreAuthorize(Permission.MITARBEITER)
    public Set<Pool> findAllPoolsOfUser(User user, Boolean findAllExistingPools) {

        // If the user is an admin and all pools should found, then return all existing pools
        if (user.getRole().getRoleName().equals(Role.ADMINISTRATOR) && findAllExistingPools) {
            return new HashSet<>(poolRepository.findAll());
        }

        Optional<User> optionalUser = userRepository.findById(user.getId());

        return optionalUser.isPresent() ? new HashSet<>(optionalUser.get().getPools()) : Collections.emptySet();
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

    /**
     * This method uses the poolRepository to add a user to a pool
     *
     *
     */
    @PreAuthorize(Permission.VORGESETZTER)
    public void addUserToPool (Pool pool, User user){

        Set<User> Users = pool.getUsers();
        Users.add(user);
        pool.setUsers(Users);
        poolRepository.save(pool);
    }
}
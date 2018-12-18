package de.gruppe2.agamoTTTo.service;

import de.gruppe2.agamoTTTo.domain.entity.Pool;
import de.gruppe2.agamoTTTo.domain.entity.User;
import de.gruppe2.agamoTTTo.repository.PoolRepository;
import de.gruppe2.agamoTTTo.repository.UserRepository;
import de.gruppe2.agamoTTTo.security.SecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
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
    public void addPool(Pool pool){
        poolRepository.save(pool);
    }

    /**
     * This method uses the poolRepository to try to update to the database.
     *
     * @param updatedPool the updated pool as obtained from the controller
     */
    public void updatePool(Pool updatedPool){
        // Use the getOne method, so that no more DB fetch has to be executed
        Pool poolToUpdate = poolRepository.getOne(updatedPool.getId());

        poolToUpdate.setName(updatedPool.getName());
        poolRepository.save(poolToUpdate);
    }

    /**
     * This method uses the poolRepository to find all pools from the database.
     *
     * @return all pools in the database
     */
    public Set<Pool> findAllPools() {
        return new HashSet<>(poolRepository.findAll());
    }

    /**
     * This method uses the userRepository to find all pools which the logged in user is part of.
     *
     * @return pools which the logged in user is part of
     */
    public Set<Pool> findAllPoolsOfAuthenticationUser(){
        Optional<User> optionalUser = userRepository.findById(SecurityContext.getAuthenticationUser().getId());

        return optionalUser.isPresent() ? new HashSet<>(optionalUser.get().getPools()) : Collections.emptySet();
    }

    /**
     * This method uses the poolRepository to find a certain pool from the database.
     * If no pool was found, an empty optional object is returned.
     *
     * @param id the id of the pool, which should be found
     * @return the optional pool
     */
    public Optional<Pool> findPoolById(Long id){
        return poolRepository.findById(id);
    }

}
package de.gruppe2.agamoTTTo.service;

import de.gruppe2.agamoTTTo.domain.entity.Pool;
import de.gruppe2.agamoTTTo.domain.entity.UserPool;
import de.gruppe2.agamoTTTo.repository.PoolRepository;
import de.gruppe2.agamoTTTo.repository.UserPoolRepository;
import de.gruppe2.agamoTTTo.security.Permission;
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
        // Save the pool to the database.
        poolRepository.save(pool);

        // Assign the owner of the pool to the newly created pool
        userPoolRepository.save(new UserPool(pool.getOwner(), pool));
    }

    /**
     * This method uses the userPoolRepository to find all pools.
     *
     * @return all pools of the database
     */
    @PreAuthorize(Permission.ADMINISTRATOR)
    public List<Pool> findAllPools() {
        return poolRepository.findAllByOrderByNameAsc();
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

        // Manipulate the poolToUpdate with the updated with fields
        poolToUpdate.setName(updatedPool.getName());

        // Save the pool to the database.
        poolRepository.save(poolToUpdate);
    }
}
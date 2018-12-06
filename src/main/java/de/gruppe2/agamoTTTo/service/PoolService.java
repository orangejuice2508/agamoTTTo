package de.gruppe2.agamoTTTo.service;

import de.gruppe2.agamoTTTo.entity.Pool;
import de.gruppe2.agamoTTTo.repository.PoolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service which is used for dealing with the pools("Arbeitsbereiche") of our application.
 */
@Service
public class PoolService {

    private PoolRepository poolRepository;

    @Autowired
    public PoolService(PoolRepository poolRepository) {
        this.poolRepository = poolRepository;
    }

    /**
     * This method uses the poolRepository to try to add a pool to the database.
     *
     * @param pool the pool as obtained from the controller
     */
    public void addPool(Pool pool){
        poolRepository.save(pool);
    }

    public List<Pool> getAllPools() { return poolRepository.findAll(); }

}

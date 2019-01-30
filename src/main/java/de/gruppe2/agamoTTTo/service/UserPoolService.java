package de.gruppe2.agamoTTTo.service;

import de.gruppe2.agamoTTTo.domain.entity.Pool;
import de.gruppe2.agamoTTTo.domain.entity.User;
import de.gruppe2.agamoTTTo.domain.entity.UserPool;
import de.gruppe2.agamoTTTo.repository.UserPoolRepository;
import de.gruppe2.agamoTTTo.security.Permission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service which is used for dealing with the userPool assignments("Zuordnungen") of our application.
 */
@Service
public class UserPoolService {

    private UserPoolRepository userPoolRepository;

    private EmailService emailService;

    private MessageSource messageSource;

    @Autowired
    public UserPoolService(UserPoolRepository userPoolRepository, EmailService emailService, MessageSource messageSource) {
        this.userPoolRepository = userPoolRepository;
        this.emailService = emailService;
        this.messageSource = messageSource;
    }

    /**
     * This method uses the UserPoolRepository to add a userPool assignment to the database.
     * If the userPool assignment is already in the database, but the userPool assignment is INACTIVE,
     * then the userPool assignment is set ACTIVE in the database.
     *
     * @param userPool the entity which combines a user and a pool
     */
    @PreAuthorize(Permission.VORGESETZTER)
    public void addUserPool(UserPool userPool) {
        // Check whether the userPool assignment is already in the database but set inactive.
        Optional<UserPool> optionalUserPool =
                userPoolRepository.findByUserAndPoolAndIsActiveIsFalse(userPool.getUser(), userPool.getPool());

        // If so, then change the status to active. If not, store the userPool assignment in the optional.
        if (optionalUserPool.isPresent()) {
            optionalUserPool.get().setIsActive(Boolean.TRUE);
        } else {
            optionalUserPool = Optional.of(userPool);
        }

        userPool = optionalUserPool.get();

        // Save the userPool assignment to the database.
        userPoolRepository.save(userPool);

        /* If the user was added successfully to the pool the email can be sent.
           Note: We obtain the subject and text of the email from "messages.properties" by using the messageSource.
           Since the text of the email contains parameters (pool name) we need to pass them to the messageSource.
         */
        String subject = messageSource.getMessage("pools.assignments.add.email.subject", null, Locale.getDefault());
        Object[] parameters = {userPool.getPool().getName()};
        String text = messageSource.getMessage("pools.assignments.add.email.text", parameters, Locale.getDefault());
        emailService.sendHTMLEmail(userPool.getUser().getEmail(), subject, text);
    }


    /**
     * This method uses the UserPoolRepository to find all userPool assignments of the specified pool.
     *
     * @param pool the pool of which the userPool assignments should be found.
     * @param onlyActiveUserPools if true, then only ACTIVE assignments are returned. if false, also INACTIVE assignments are returned.
     * @return userPools userPool assignments of the pool, either only active ones or all.
     */
    @PreAuthorize(Permission.VORGESETZTER)
    public List<UserPool> findAllUserPools(Pool pool, Boolean onlyActiveUserPools) {
        // Get all userPool assignments of the pool and sort it by the lastName of the user.
        List<UserPool> userPools = userPoolRepository.findAllByPool(pool)
                .stream()
                .sorted(Comparator.comparing(userPool -> userPool.getUser().getLastName()))
                .collect(Collectors.toList());

        // If only active userPool assignments should be returned, then remove all inactive ones.
        if (onlyActiveUserPools) {
            userPools = userPools.stream().filter(UserPool::getIsActive).collect(Collectors.toList());
        }

        return userPools;
    }

    /**
     * This method uses the UserPoolRepository to find all userPool assignments of the specified user.
     *
     * @param user the user of which the userPool assignments should be found.
     * @param onlyActiveUserPools if true, then only ACTIVE assignments are returned. if false, also INACTIVE assignments are returned.
     * @return userPools userPool assignments of the user, either only active ones or all.
     */
    @PreAuthorize(Permission.MITARBEITER)
    public List<UserPool> findAllUserPools(User user, Boolean onlyActiveUserPools) {
        // Get all userPool assignments of the user and sort it by the name of the pool.
        List<UserPool> userPools = userPoolRepository.findAllByUser(user)
                .stream()
                .sorted(Comparator.comparing(userPool -> userPool.getPool().getName()))
                .collect(Collectors.toList());

        // If only active userPool assignments should be returned, then remove all inactive ones.
        if (onlyActiveUserPools) {
            userPools = userPools.stream().filter(UserPool::getIsActive).collect(Collectors.toList());
        }

        return userPools;
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
        // When the list of userPools is NOT empty, then the specified user is actively assigned to the specified pool.
        return !pool.getUserPools()
                .stream()
                .filter(userPool -> userPool.getUser().getId().equals(user.getId()))
                .filter(UserPool::getIsActive)
                .collect(Collectors.toSet())
                .isEmpty();
    }

    /**
     * This method uses the UserPool repository to "remove" a userPool assignment from the database.
     * Note: The assignment is not removed but set inactive.
     *
     * @param userPool the assignment of a user to a pool that should be removed
     */
    @PreAuthorize(Permission.VORGESETZTER)
    public void removeUserPool(UserPool userPool) {
        // Use the getOne method, so that no more DB fetch has to be executed.
        userPool = userPoolRepository.getOne(userPool.getId());

        // "Remove" the record by setting the isActive field to false.
        userPool.setIsActive(Boolean.FALSE);

        // Save the updated assignment to the database
        userPoolRepository.save(userPool);

        /* If the user was "removed" successfully to the pool the email can be sent.
           Note: We obtain the subject and text of the email from "messages.properties" by using the messageSource.
           Since the text of the email contains parameters (pool name) we need to pass them to the messageSource.
         */
        String subject = messageSource.getMessage("pools.assignments.remove.email.subject", null, Locale.getDefault());
        Object[] parameters = {userPool.getPool().getName()};
        String text = messageSource.getMessage("pools.assignments.remove.email.text", parameters, Locale.getDefault());
        emailService.sendHTMLEmail(userPool.getUser().getEmail(), subject, text);
    }
}

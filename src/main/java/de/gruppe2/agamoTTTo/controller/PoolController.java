package de.gruppe2.agamoTTTo.controller;

import de.gruppe2.agamoTTTo.domain.bo.filter.PoolDateFilter;
import de.gruppe2.agamoTTTo.domain.entity.Pool;
import de.gruppe2.agamoTTTo.domain.entity.User;
import de.gruppe2.agamoTTTo.domain.entity.UserPool;
import de.gruppe2.agamoTTTo.security.Permission;
import de.gruppe2.agamoTTTo.security.Role;
import de.gruppe2.agamoTTTo.security.SecurityContext;
import de.gruppe2.agamoTTTo.service.PoolService;
import de.gruppe2.agamoTTTo.service.RecordService;
import de.gruppe2.agamoTTTo.service.UserPoolService;
import de.gruppe2.agamoTTTo.service.UserService;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * This controller is used for mapping all requests to /pools/
 */
@Controller
@RequestMapping("pools")
public class PoolController extends BaseController {

    private RecordService recordService;

    private PoolService poolService;

    private UserService userService;

    private UserPoolService userPoolService;

    private MessageSource messageSource;

    @Autowired
    public PoolController(RecordService recordService,
                          PoolService poolService,
                          UserService userService,
                          UserPoolService userPoolService,
                          MessageSource messageSource) {
        this.recordService = recordService;
        this.poolService = poolService;
        this.userService = userService;
        this.userPoolService = userPoolService;
        this.messageSource = messageSource;
    }

    /**
     * Method for displaying the "add new pool" page.
     *
     * @param model the Spring Model
     * @return path to template
     */
    @PreAuthorize(Permission.VORGESETZTER)
    @GetMapping("/add")
    public String getAddPoolPage(Model model) {
        model.addAttribute("pool", new Pool());
        return "pools/add";
    }

    /**
     * Method for handling the submission of the "add new pool" form.
     *
     * @param pool the pool as obtained from the form
     * @param bindingResult contains possible form errors
     * @return path to resulting template
     */
    @PostMapping("/add")
    public String postAddPoolPage(@ModelAttribute @Valid Pool pool, BindingResult bindingResult) {
        /* If the form contains errors, the new pool won't be added and the form is displayed again with
           corresponding error messages. */
        if(bindingResult.hasErrors()) {
            return "pools/add";
        }

        /* Try to add the pool to the database. If the pool name exists already, a DataIntegrityViolation
        will be thrown by the PoolService/PoolRepository. Then the form is shown again with a corresponding
        error message.
        */
        try {
            poolService.addPool(pool);
        } catch (DataIntegrityViolationException e) {
            bindingResult.rejectValue("name", "error.pool", messageSource.getMessage("pools.error.name_not_unique", null, Locale.getDefault()));
            return "pools/add";
        }

        // If the pool was added successfully, reload the page with an empty form.
        return "redirect:/pools/add/?successful=true";
    }

    /**
     * Method for displaying the "overview pool" page.
     *
     * @param model the Spring Model
     * @return path to template
     */
    @GetMapping("/overview")
    public String getOverviewPoolsPage(Model model){
        // Get the logged in user to determine their role.
        User authenticationUser = SecurityContext.getAuthenticationUser();

        // Add the pools to the model
        model.addAttribute("pools", getAllPoolsOfUser(authenticationUser));

        /* In the view we need the id of the logged in user to determine whether he is
        entitled to edit a pool. If the user is an admin, he is entitled to edit every pool */
        if (!authenticationUser.getRole().getRoleName().equals(Role.ADMINISTRATOR)) {
            model.addAttribute("authenticationUserId", authenticationUser.getId());
        }

        return "pools/overview";
    }

    /**
     * Method for displaying the edit form for a pool determined by its id.
     *
     * @param id a pool's id as specified in the url
     * @param model the Spring Model
     * @return path to the template
     * @throws Exception if no pool with the id could be found in the DB or the user is not entitled to edit the pool
     */
    @GetMapping("/edit/{id}")
    public String getEditPoolPage(@PathVariable("id") Long id, Model model) throws Exception {
        // Get the pool with the specified id from the database
        Optional<Pool> optionalPool = poolService.findPoolById(id);
        // Get the currently logged in user
        User authenticationUser = SecurityContext.getAuthenticationUser();

        // Check whether a pool with the id could be found.
        if(!optionalPool.isPresent()){
            throw new NotFoundException("No pool found with ID: " + id);
        }

        // Check whether the currently logged in user is the owner of the pool. The admin is allowed to edit every pool.
        if (!optionalPool.get().getOwner().getId().equals(authenticationUser.getId()) && !authenticationUser.getRole().getRoleName().equals(Role.ADMINISTRATOR)) {
            throw new AccessDeniedException("The user/editor " + authenticationUser.getEmail() + " is not entitled to edit the specified pool");
        }

        // Add the pool which should be edited to the model
        model.addAttribute("pool", optionalPool.get());

        return "pools/edit";
    }

    /**
     * Method for handling the submission of the "edit pool" form.
     *
     * @param updatedPool the pool with updated fields
     * @param bindingResult contains possible form errors
     * @return path to the template
     */
    @PutMapping("/edit")
    public String putEditPoolPage(@Valid Pool updatedPool, BindingResult bindingResult) {
        /* If the form contains errors, the pool won't be updated and the form is displayed again with
        corresponding error messages. */
        if(bindingResult.hasErrors()){
            return "pools/edit";
        }

        /* Try to update the pool in the database. If the pool name exists already, a DataIntegrityViolation
        will be thrown by the PoolService/PoolRepository. Then the form is shown again with a corresponding
        error message.
        */
        try {
            poolService.updatePool(updatedPool);
        } catch (DataIntegrityViolationException e) {
            bindingResult.rejectValue("name", "error.pool", messageSource.getMessage("pools.error.name_not_unique", null, Locale.getDefault()));
            return "pools/edit";
        }

        // If the pool was updated successfully, redirect to the pools' overview page.
        return "redirect:/pools/overview/?successful=true&mode=editPool";
    }

    /**
     * Method for displaying the assignments of a pool determined by its id.
     *
     * @param id    the id of the pool which assignments should be shown
     * @param model the Spring model
     * @return path to the template
     * @throws Exception if no pool with the id could be found in the DB or the user is not entitled to edit the pool
     */
    @GetMapping("/assignments/show/{id}")
    public String getShowAssignmentPage(@PathVariable Long id, Model model) throws Exception {
        // Check poolId + permission and get pool object.
        Pool pool = getPool(id, SecurityContext.getAuthenticationUser());

        // Get all users that are or used to be assigned to the previously selected pool.
        List<UserPool> userPools = userPoolService.findAllUserPools(pool, false);

        // Add the pool and all userPool assignments
        model.addAttribute("pool", pool);
        model.addAttribute("userPools", userPools);

        return "pools/assignments/show";
    }

    /**
     * Method for displaying the form for adding a user to a pool determined by its id (i.e. adding an assignment).
     *
     * @param id the id of the pool which a user should be added to
     * @param model the Spring model
     * @return path to the template
     * @throws Exception if no pool with the id could be found in the DB or the user is not entitled to edit the pool
     */
    @GetMapping("/assignments/add/{id}")
    public String getAddAssignmentPage(@PathVariable Long id, Model model) throws Exception {
        // Check poolId + permission and get pool object.
        Pool pool = getPool(id, SecurityContext.getAuthenticationUser());

        // Create a new UserPool entity with the previously selected pool
        UserPool userPool = new UserPool(pool);

        // Get all users that are currently NOT in the previously selected pool
        List<User> allUsersNotInPool = userService.getAllUsersNotInPool(pool);

        // Add the userPool and allUsersNotInPool to the model
        model.addAttribute("userPool", userPool);
        model.addAttribute("users", allUsersNotInPool);

        return "pools/assignments/add";
    }

    /**
     * Method for handling the submission of the "add assignment" form.
     *
     * @param userPool the new assignment of a user to a pool
     * @return path to template
     */
    @PostMapping("/assignments/add")
    public String postAddAssignmentPage(@ModelAttribute UserPool userPool) {
        // Add the assignment to the database
        userPoolService.addUserPool(userPool);

        // If the assignment was added successfully, redirect to the assignments' overview page.
        return "redirect:/pools/overview?successful=true&mode=addAssignment";
    }

    /**
     * Method for displaying the form for removing users from a a pool determined by its id (i.e. setting an
     * assignment inactive).
     *
     * @param id    the id of the pool which a user should be removed from
     * @param model the Spring model
     * @return path to the template
     * @throws Exception if no pool with the id could be found in the DB or the user is not entitled to edit the pool
     */
    @GetMapping("/assignments/remove/{id}")
    public String getRemoveAssignmentPage(@PathVariable Long id, Model model) throws Exception {
        // Check poolId + permission and get pool object.
        Pool pool = getPool(id, SecurityContext.getAuthenticationUser());

        /*
         Get all users that are currently assigned to the previously selected pool.
         Then remove the owner of the specific pool, because he can not be removed from the pool.
         */
        List<UserPool> allActiveUserPools = userPoolService.findAllUserPools(pool, true)
                .stream()
                .filter(userPool -> !userPool.getUser().equals(pool.getOwner()))
                .collect(Collectors.toList());

        // Add the chosen pool, the active assignments and an empty userPool object to the model.
        model.addAttribute("pool", pool);
        model.addAttribute("userPools", allActiveUserPools);
        model.addAttribute("userPoolToRemove", new UserPool());

        return "pools/assignments/remove";
    }

    /**
     * Method for handling the submission of the "remove assignment" form.
     * Note: The assignment is not deleted but set inactive.
     *
     * @param userPoolToRemove the assignment of a user to a pool that should be removed
     * @return path to template
     */
    @PostMapping("/assignments/remove")
    public String postRemoveAssignmentPage(@ModelAttribute UserPool userPoolToRemove) {
        // Set the assignment of a user to a pool inactive.
        userPoolService.deleteUserPool(userPoolToRemove);

        // If the assignment was added successfully, redirect to the assignments' overview page.
        return "redirect:/pools/overview?successful=true&mode=removeAssignment";
    }

    /**
     * Method for displaying the analysis page of the pools.
     *
     * @param model the Spring model
     * @return path to the template
     */
    @GetMapping("/analysis")
    public String getAnalysePoolPage(Model model) {
        // Map all pools of a user to UserPool objects, since the HTML fragment of the filter requires that.
        List<UserPool> userPools = getAllPoolsOfUser(SecurityContext.getAuthenticationUser())
                .stream()
                .map(UserPool::new)
                .collect(Collectors.toList());

        // Add the userPool assignments and the filter to the model.
        model.addAttribute("userPools", userPools);
        model.addAttribute("filter", new PoolDateFilter(LocalDate.now()));

        return "pools/analysis";
    }

    @GetMapping("/analysis/filter")
    public String postAnalysisPoolPage(@ModelAttribute PoolDateFilter filter, Model model) {
        // Update filter so that potentially wrong dates are corrected
        filter = new PoolDateFilter(filter);

        // Map all pools of a user to UserPool objects, since the HTML fragment of the filter requires that.
        List<UserPool> userPools = getAllPoolsOfUser(SecurityContext.getAuthenticationUser())
                .stream()
                .map(UserPool::new)
                .collect(Collectors.toList());

        // Get the hash map consisting of userPool assignments and durations
        HashMap<UserPool, Long> durationPerUser = recordService.analyseRecords(filter);

        // Calculate the total duration of the retrieved records.
        Long totalDuration = durationPerUser.values().stream().mapToLong(Long::longValue).sum();

        // Add the userPool assignments, the filter, the duration of every user and the total duration to the model.
        model.addAttribute("userPools", userPools);
        model.addAttribute("filter", filter);
        model.addAttribute("durationPerUser", durationPerUser);
        model.addAttribute("totalDuration", totalDuration);

        return "pools/analysis";
    }

    /**
     * This method takes the user to determine the pools of which they are entitled to
     * edit the assignments. An admin can edit the assignments of all pools,
     * supervisors only those which they are assigned to.
     *
     * @param user the user whose entitlement should be checked
     * @return the pools of which the assignments can be edited by the user
     */
    private List<Pool> getAllPoolsOfUser(User user) {
        // The admin can see all pools, all others only the pools they're assigned to.
        if (user.getRole().getRoleName().equals(Role.ADMINISTRATOR)) {
            // Return all existing pools
            return poolService.findAllPools();
        } else {
            // Return only those pools which the user is currently assigned to
            return userPoolService.findAllUserPools(user, true).stream()
                    .map(UserPool::getPool)
                    .collect(Collectors.toList());
        }
    }

    /**
     * This method uses the id of a pool and a user object to check whether
     * the pool id exists and whether the user is assigned to the pool.
     * If both is true, a plain pool object is returned.
     *
     * @param poolId the id of the pool which should be checked
     * @param user   the user whose assignment to the pool should be checked
     * @return a plain pool object which corresponds to the poolId
     * @throws NotFoundException     if no pool with the specified id could be found in the database
     * @throws AccessDeniedException if the currently logged in user is not entitled to edit ths pool
     */
    private Pool getPool(Long poolId, User user) throws NotFoundException, AccessDeniedException {
        // Try to get the pool specified by its id from the database
        Optional<Pool> optionalPool = poolService.findPoolById(poolId);

        // Check whether a pool with the id could be found.
        if (!optionalPool.isPresent()) {
            throw new NotFoundException("No pool found with ID: " + poolId);
        }

        // Check whether the user is assigned to this pool. The admin is allowed to edit every pool.
        if (!userPoolService.isUserInPool(user, optionalPool.get()) && !user.getRole().getRoleName().equals(Role.ADMINISTRATOR)) {
            throw new AccessDeniedException("The user/editor " + user.getEmail() + " is not entitled to edit the specified pool");
        }

        return optionalPool.get();
    }
}

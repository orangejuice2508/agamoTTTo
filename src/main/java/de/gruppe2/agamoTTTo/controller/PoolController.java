package de.gruppe2.agamoTTTo.controller;

import de.gruppe2.agamoTTTo.domain.entity.Pool;
import de.gruppe2.agamoTTTo.domain.entity.User;
import de.gruppe2.agamoTTTo.security.Permission;
import de.gruppe2.agamoTTTo.security.Role;
import de.gruppe2.agamoTTTo.security.SecurityContext;
import de.gruppe2.agamoTTTo.service.PoolService;
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
import java.util.Locale;
import java.util.Optional;

@Controller
@RequestMapping("pools")
public class PoolController extends BaseController {

    private PoolService poolService;

    private MessageSource messageSource;

    @Autowired
    public PoolController(PoolService poolService, MessageSource messageSource) {
        this.poolService = poolService;
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
    public String getAddPoolsPage(Model model) {
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
    @PreAuthorize(Permission.VORGESETZTER)
    @PostMapping("/add")
    public String postAddPoolsPage(@ModelAttribute @Valid Pool pool, BindingResult bindingResult) {
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
        }
        catch(DataIntegrityViolationException e) {
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
    @PreAuthorize(Permission.VORGESETZTER)
    @GetMapping("/overview")
    public String getOverviewPoolsPage(Model model){
        // Get the logged in user to determine their role.
        User authenticationUser = SecurityContext.getAuthenticationUser();

        // The admin can see all pools, all others only the pools they're assigned to.
        if(authenticationUser.getRole().getRoleName().equals(Role.ADMINISTRATOR)) {
            model.addAttribute("pools", poolService.findAllPools());
        }
        else {
            model.addAttribute("pools", poolService.findAllPoolsOfAuthenticationUser());
            /* In the view we need the id of the logged in user to determine whether he is
            entitled to edit a pool. */
            model.addAttribute("userId", authenticationUser.getId());
        }

        return "pools/overview";
    }

    /**
     * Method for displaying the edit form for a pool determined by its id.
     *
     * @param id a pool's id as specified in the path
     * @param model the Spring Model
     * @return path to the template
     * @throws NotFoundException if no pool with the id can be found in the DB
     */
    @PreAuthorize(Permission.VORGESETZTER)
    @GetMapping("/edit/{id}")
    public String getEditPoolPage(@PathVariable("id") Long id, Model model) throws Exception {

        Optional<Pool> optionalPool = poolService.findPoolById(id);

        // Check whether a pool with the id could be found.
        if(!optionalPool.isPresent()){
            throw new NotFoundException("No pool found with ID: " + id);
        }

        // Check whether the current user is allowed to edit this pool.
        if(!optionalPool.get().getOwner().getId().equals(SecurityContext.getAuthenticationUser().getId())){
            throw new AccessDeniedException("The current user/editor and the pool owner are not identical.");
        }

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
    @PreAuthorize(Permission.VORGESETZTER)
    @PutMapping("/edit/{id}")
    public String postEditPoolPage(@PathVariable Long id, @Valid Pool updatedPool, BindingResult bindingResult) {
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
        }
        catch(DataIntegrityViolationException e) {
            bindingResult.rejectValue("name", "error.pool", messageSource.getMessage("pools.error.name_not_unique", null, Locale.getDefault()));
            return "pools/edit";
        }

        // If the pool was updated successfully, redirect to the pools' overview page.
        return "redirect:/pools/overview/?successful=true";
    }
}

package de.gruppe2.agamoTTTo.controller;

import de.gruppe2.agamoTTTo.domain.entity.Pool;
import de.gruppe2.agamoTTTo.security.Permission;
import de.gruppe2.agamoTTTo.service.PoolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.util.Locale;

@Controller
@RequestMapping("pools")
public class PoolController  extends de.gruppe2.agamoTTTo.controller.Controller {

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
    public String getAddPoolsPage(Model model){
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
    public String postAddPoolsPage(@ModelAttribute @Valid Pool pool, BindingResult bindingResult){
        /* If the form contains errors, the new pool won't be added and the form is displayed again with
           corresponding error messages. */
        if(bindingResult.hasErrors()){
            return "pools/add";
        }
        else{
            /* Try to add the pool to the database. If the pool name exists already, a DataIntegrityViolation
                will be thrown by the PoolService/PoolRepository. Then the form is shown again with a corresponding
                error message.
             */
            try{
                poolService.addPool(pool);
            }
            catch(DataIntegrityViolationException e){
                bindingResult.rejectValue("name", "error.pool", messageSource.getMessage("pools.add.error.name_not_unique", null, Locale.getDefault()));
                return "pools/add";
            }
        }

        // If the user was added successfully, reload the page with an empty form.
        return "redirect:/pools/add/?successful=true";
    }

    /**
     * Method for displaying the "overview pool" page.
     * Note: Authorization is still "ROLE_ADMINISTRATOR",
     * because showing a user HIS pools is not implemented yet.
     *
     * @param model the Spring Model
     * @return path to template
     */
    @PreAuthorize(Permission.ADMINISTRATOR)
    @GetMapping("/overview")
    public String getOverviewPools(Model model){
        model.addAttribute("pools", poolService.getAllPools());
        return "pools/overview";
    }
}

package de.gruppe2.agamoTTTo.controller;

import de.gruppe2.agamoTTTo.domain.entity.Pool;
import de.gruppe2.agamoTTTo.security.Permission;
import de.gruppe2.agamoTTTo.service.PoolService;
import de.gruppe2.agamoTTTo.service.RecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Locale;

@Controller
@RequestMapping("pools")
public class RecordController extends de.gruppe2.agamoTTTo.controller.Controller{

    private PoolService poolService;

    private RecordService recordService;

    private MessageSource messageSource;

    @Autowired
    public RecordController(RecordService recordService, MessageSource messageSource) {
        this.poolService = poolService;
        this.messageSource = messageSource;

    }

    /**
     * Method for displaying the "add new record" page.
     *
     * @param model the Spring Model
     * @return path to template
     */
    public String getAddPoolsPage(Model model) {
        model.addAttribute("pool", new Pool());
        return "pools/add";
    }

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
            return "pools/edit";
        }

        // If the pool was added successfully, reload the page with an empty form.
        return "redirect:/pools/add/?successful=true";
    }
}

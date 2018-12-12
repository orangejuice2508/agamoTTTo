package de.gruppe2.agamoTTTo.controller;

import de.gruppe2.agamoTTTo.domain.entity.Pool;
import de.gruppe2.agamoTTTo.domain.entity.User;
import de.gruppe2.agamoTTTo.repository.RecordRepository;
import de.gruppe2.agamoTTTo.repository.UserRepository;
import de.gruppe2.agamoTTTo.repository.PoolRepository;
import de.gruppe2.agamoTTTo.domain.entity.Record;
import de.gruppe2.agamoTTTo.security.Permission;
import de.gruppe2.agamoTTTo.security.SecurityContext;
import de.gruppe2.agamoTTTo.service.PoolService;
import de.gruppe2.agamoTTTo.service.RecordService;
import org.hibernate.dialect.HANAColumnStoreDialect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.context.MessageSource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

@Controller
@RequestMapping("records")
public class RecordController extends de.gruppe2.agamoTTTo.controller.Controller{

    private RecordService recordService;
    private PoolService poolService;
    private MessageSource messageSource;

    @Autowired
    public RecordController(RecordService recordService, PoolService poolService, MessageSource messageSource) {
        this.recordService = recordService;
        this.poolService = poolService;
        this.messageSource = messageSource;
    }

    /**
     * Method for displaying the "add new record" page.
     *
     * @param model the Spring Model
     * @return path to template
     */
    @PreAuthorize(Permission.MITARBEITER)
    @GetMapping("/add")
    public String getAddRecordPage(Model model) {
        // Get the logged in user to determine their role.
        User authenticationUser = SecurityContext.getAuthenticationUser();
        model.addAttribute("pools", poolService.findAllPoolsOfAUser(authenticationUser));
        model.addAttribute("record", new Record());
        return "records/add";
    }

    @PostMapping("/add")
    public String postAddRecordPage(@ModelAttribute @Valid Record record, BindingResult bindingResult) {

        /* If the form contains errors, the new record won't be added and the form is displayed again with
           corresponding error messages. */
        if(bindingResult.hasErrors()) {
            return "records/add";
        }

        /* Try to add the pool to the database. If the pool name exists already, a DataIntegrityViolation
        will be thrown by the PoolService/PoolRepository. Then the form is shown again with a corresponding
        error message.
        */
        try {
            recordService.addRecord(record);
            recordService.getAllRecordsOfAUser(SecurityContext.getAuthenticationUser());
        }
        catch (Exception e){
            return "records/add";
        }
        return "redirect:/records/add/?successful=true";
    }


}

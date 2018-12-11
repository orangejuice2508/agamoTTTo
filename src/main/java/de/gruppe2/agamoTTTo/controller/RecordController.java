package de.gruppe2.agamoTTTo.controller;

import de.gruppe2.agamoTTTo.domain.entity.Pool;
import de.gruppe2.agamoTTTo.domain.entity.Record;
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
@RequestMapping("records")
public class RecordController extends de.gruppe2.agamoTTTo.controller.Controller{

    private RecordService recordService;
    private MessageSource messageSource;

    @Autowired
    public RecordController(RecordService recordService, MessageSource messageSource) {
        this.recordService = recordService;
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
        model.addAttribute("record", new Record());
        return "records/add";
    }

}

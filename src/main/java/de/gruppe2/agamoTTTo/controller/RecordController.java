package de.gruppe2.agamoTTTo.controller;

import de.gruppe2.agamoTTTo.domain.base.filter.PoolDateFilter;
import de.gruppe2.agamoTTTo.domain.entity.Record;
import de.gruppe2.agamoTTTo.domain.entity.User;
import de.gruppe2.agamoTTTo.security.Permission;
import de.gruppe2.agamoTTTo.security.SecurityContext;
import de.gruppe2.agamoTTTo.service.PoolService;
import de.gruppe2.agamoTTTo.service.RecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

@Controller
@RequestMapping("records")
public class RecordController extends BaseController {

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

        model.addAttribute("pools", poolService.findAllPoolsOfAuthenticationUser());
        model.addAttribute("record", new Record());
        return "records/add";
    }

    /**
     * Method for handling the submission of the "add new record" form.
     *
     * @param record the record as obtained from the form
     * @param bindingResult contains possible form errors
     * @return path to resulting template
     */
    @PostMapping("/add")
    public String postAddRecordPage(@ModelAttribute @Valid Record record, BindingResult bindingResult, Model model) {

        // Check whether the record is valid
        checkRecord(record, bindingResult);

        /* If the form contains errors, the new record won't be added and the form is displayed again with
           corresponding error messages. */
        if (bindingResult.hasErrors()) {
            model.addAttribute("pools", poolService.findAllPoolsOfAuthenticationUser());
            return "records/add";
        }

        // Else: add the record to the database
        recordService.addRecord(record);
        return "redirect:/records/add/?successful=true";
    }

    @GetMapping("/edit")
    public String getEditRecordPage() {
        return null;
    }

    /**
     * Method for displaying the "analysis" page for the records.
     *
     * @param model the Spring Model
     * @return path to template
     */
    @GetMapping("/analysis")
    public String getAnalyseRecordPage(Model model){

        model.addAttribute("filter", new PoolDateFilter(LocalDate.now()));
        model.addAttribute("pools", poolService.findAllPoolsOfAuthenticationUser());

        return "records/analysis";
    }

    /**
     * Method for handling the submission of the filter on the "analysis" page.
     *
     * @param filter contains criteria set by the user on the overview page
     * @param model the Spring Model
     * @return path to template
     */
    @PostMapping("/analysis")
    public String postAnalyseRecordPage(@ModelAttribute PoolDateFilter filter,  Model model){

        model.addAttribute("pools", poolService.findAllPoolsOfAuthenticationUser());
        model.addAttribute("filter", filter);

        List<Record> records = recordService.getAllRecordsByFilter(filter, SecurityContext.getAuthenticationUser());

        // Calculate total duration of the retrieved records
        Long totalDuration = records.stream().mapToLong(Record::getDuration).sum();

        model.addAttribute("records", records);
        model.addAttribute("totalDuration", totalDuration);


        return "records/analysis";
    }

    /**
     * Method for displaying the "overview" page for the records.
     *
     * @param model the Spring Model
     * @return path to template
     */
    @GetMapping("/overview")
    public String getOverviewRecordPage(Model model) {

        model.addAttribute("filter", new PoolDateFilter(LocalDate.now()));

        return "records/overview";
    }

    /**
     * Method for handling the submission of the filter on the "overview" page.
     *
     * @param filter contains criteria set by the user on the overview page.
     * @param model the Spring Model
     * @return path to template
     */
    @PostMapping("/overview")
    public String postOverviewRecordPage(@ModelAttribute PoolDateFilter filter, Model model) {

        model.addAttribute("filter", filter);

        List<Record> records = recordService.getAllRecordsByDate(filter, SecurityContext.getAuthenticationUser());

        model.addAttribute("records", records);

        return "records/overview";
    }

    /**
     * This method checks a record if it contains valid times. If it's not valid, the BindingResult will
     * be manipulated so that it contains the corresponding error messages.
     *
     * @param record the record which should be created/edited
     * @param bindingResult contains possible form errors
     *
     */
    private void checkRecord(Record record, BindingResult bindingResult) {

        // Check if the record's times are valid
        if (!recordService.areTimesValid(record)) {
            bindingResult.rejectValue("startTime", "error.record", messageSource.getMessage("records.error.wrong_starttime_and_wrong_endtime", null, Locale.getDefault()));
            bindingResult.rejectValue("endTime", "error.record", messageSource.getMessage("records.error.wrong_starttime_and_wrong_endtime", null, Locale.getDefault()));
        }

        // Get the logged in user.
        User authenticationUser = SecurityContext.getAuthenticationUser();

        // Check if the user already has a record that overlaps with this one
        if (!recordService.areTimesAllowed(record, authenticationUser)) {
            bindingResult.rejectValue("startTime", "error.record", messageSource.getMessage("records.error.entry_already_exists", null, Locale.getDefault()));
            bindingResult.rejectValue("endTime", "error.record", messageSource.getMessage("records.error.entry_already_exists", null, Locale.getDefault()));
        }
    }
}
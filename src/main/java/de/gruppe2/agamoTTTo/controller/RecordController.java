package de.gruppe2.agamoTTTo.controller;

import de.gruppe2.agamoTTTo.util.ExcelGenerator;
import de.gruppe2.agamoTTTo.domain.base.filter.PoolDateFilter;
import de.gruppe2.agamoTTTo.domain.entity.Record;
import de.gruppe2.agamoTTTo.domain.entity.User;
import de.gruppe2.agamoTTTo.security.SecurityContext;
import de.gruppe2.agamoTTTo.service.PoolService;
import de.gruppe2.agamoTTTo.service.RecordService;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Controller
@RequestMapping("records")
public class RecordController extends BaseController {

    private RecordService recordService;
    private PoolService poolService;
    private MessageSource messageSource;
    private ExcelGenerator excelGenerator;

    @Autowired
    public RecordController(RecordService recordService, PoolService poolService, MessageSource messageSource, ExcelGenerator excelGenerator) {
        this.recordService = recordService;
        this.poolService = poolService;
        this.messageSource = messageSource;
        this.excelGenerator = excelGenerator;
    }

    /**
     * Method for displaying the "add new record" page.
     *
     * @param model the Spring Model
     * @return path to template
     */
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
    @GetMapping(params = "send", value = "/analysis/filter")
    public String postAnalyseRecordPage(@ModelAttribute PoolDateFilter filter,  Model model){

        model.addAttribute("pools", poolService.findAllPoolsOfAuthenticationUser());
        model.addAttribute("filter", filter);

        List<Record> records = recordService.getAllRecordsByFilter(filter, SecurityContext.getAuthenticationUser());
        model.addAttribute("records", records);
        model.addAttribute("totalDuration", recordService.calculateTotalDuration(records));

        return "records/analysis";
    }

    /**
     * Method for handling the submission of the export button on the "analysis" page.
     *
     * @param filter contains criteria set by the user on the overview page.
     * @return path to template
     */
    @GetMapping(params = "export", value = "/analysis/filter")
    public ResponseEntity<InputStreamResource> excelRecordsReport(@ModelAttribute PoolDateFilter filter) {

        ByteArrayInputStream in = excelGenerator.createExcelSheet(filter, SecurityContext.getAuthenticationUser());

        String filename = "Arbeitsstunden" + filter.getFrom().toString() + "bis" + filter.getTo().toString();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=\"" + filename + ".xlsx" + "\"");

        return ResponseEntity.ok().headers(headers).body(new InputStreamResource(in));
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
        model.addAttribute("pools", poolService.findAllPoolsOfAuthenticationUser());

        return "records/overview";
    }

    /**
     * Method for handling the submission of the filter on the "overview" page.
     *
     * @param filter contains criteria set by the user on the overview page.
     * @param model the Spring Model
     * @return path to template
     */
    @GetMapping("/overview/filter")
    public String postOverviewRecordPage(@ModelAttribute PoolDateFilter filter, Model model) {

        model.addAttribute("pools", poolService.findAllPoolsOfAuthenticationUser());
        model.addAttribute("filter", filter);

        List<Record> records = recordService.getAllRecordsByFilter(filter, SecurityContext.getAuthenticationUser());
        model.addAttribute("records", records);

        return "records/overview";
    }

    /**
     * Method for displaying the edit form of a record determined by its id.
     *
     * @param id a record's id as specified in the path
     * @param model The Spring model
     * @return path to the template
     */
    @GetMapping("/edit/{id}")
    public String getEditRecordPage(@PathVariable("id") Long id, Model model) throws Exception {

        Optional<Record> optionalRecord = recordService.findRecordById(id);

        // Check whether a record with the id could be found.
        if(!optionalRecord.isPresent()){
            throw new NotFoundException("No record found with ID: " + id);
        }

        // Check whether the current user is allowed to edit this record.
        if(!optionalRecord.get().getUser().getId().equals(SecurityContext.getAuthenticationUser().getId())){
            throw new AccessDeniedException("The current user/editor and the record's creator are not identical.");
        }

        model.addAttribute("record", optionalRecord.get());
        model.addAttribute("pools", poolService.findAllPoolsOfAuthenticationUser());

        return "records/edit";
    }

    /**
     * Method for handling the submission of the "edit record" form.
     *
     * @param updatedRecord the pool with updated fields
     * @param bindingResult contains possible form errors
     * @return path to the template
     */
    @PutMapping("/edit/{id}")
    public String postEditRecordPage(@PathVariable("id") Long id, @Valid Record updatedRecord, BindingResult bindingResult,
                                     Model model) {

        // Check whether the record is valid
        checkRecord(updatedRecord, bindingResult);

        /* If the form contains errors, the new record won't be added and the form is displayed again with
           corresponding error messages. */
        if (bindingResult.hasErrors()) {
            model.addAttribute("pools", poolService.findAllPoolsOfAuthenticationUser());
            return "records/edit";
        }

        recordService.updateRecord(updatedRecord);
        return "redirect:/records/overview/?successful=true";
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

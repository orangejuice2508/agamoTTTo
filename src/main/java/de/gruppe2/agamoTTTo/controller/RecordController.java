package de.gruppe2.agamoTTTo.controller;

import de.gruppe2.agamoTTTo.domain.entity.Pool;
import de.gruppe2.agamoTTTo.domain.entity.UserPool;
import de.gruppe2.agamoTTTo.service.UserPoolService;
import de.gruppe2.agamoTTTo.util.ExcelGenerator;
import de.gruppe2.agamoTTTo.domain.base.filter.PoolDateFilter;
import de.gruppe2.agamoTTTo.domain.entity.Record;
import de.gruppe2.agamoTTTo.domain.entity.User;
import de.gruppe2.agamoTTTo.security.SecurityContext;
import de.gruppe2.agamoTTTo.service.RecordService;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.core.io.InputStreamResource;
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
import java.util.stream.Collectors;

/**
 * This controller is used for mapping all requests to /records/ to concrete HTML pages in resources/templates/records
 */
@Controller
@RequestMapping("records")
public class RecordController extends BaseController {

    private RecordService recordService;
    private UserPoolService userPoolService;
    private MessageSource messageSource;
    private ExcelGenerator excelGenerator;

    @Autowired
    public RecordController(RecordService recordService, UserPoolService userPoolService, MessageSource messageSource, ExcelGenerator excelGenerator) {
        this.recordService = recordService;
        this.userPoolService = userPoolService;
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
        // Add the active assignments and an empty record object to the model.
        model.addAttribute("userPools", userPoolService.findAllUserPools(SecurityContext.getAuthenticationUser(), true));
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
            return "records/add";
        }

        // Add the record to the database
        recordService.addRecord(record);
        // Add the active assignments to the model.
        model.addAttribute("userPools", userPoolService.findAllUserPools(SecurityContext.getAuthenticationUser(), true));

        // If the record was added successfully, redirect to an empty "add record" form
        return "redirect:/records/add/?successful=true";
    }

    /**
     * Method for displaying the "overview" page for the records.
     *
     * @param model the Spring Model
     * @return path to template
     */
    @GetMapping("/overview")
    public String getOverviewRecordPage(Model model) {
        // Add a poolDateFilter of the last month and ALL assignments of the current user to the model.
        model.addAttribute("filter", new PoolDateFilter(LocalDate.now()));
        model.addAttribute("userPools", userPoolService.findAllUserPools(SecurityContext.getAuthenticationUser(), false));

        return "records/overview";
    }

    /**
     * Method for handling the submission of the filter on the "overview" page.
     *
     * @param filter contains criteria set by the user on the overview page
     * @param model the Spring Model
     * @return path to template
     */
    @GetMapping(params = "send", value = "/overview/filter")
    public String getFilterRecordsPage(@ModelAttribute PoolDateFilter filter, Model model) {
        // Find all active AND inactive assignments of the currently logged in user
        List<UserPool> userPools = userPoolService.findAllUserPools(SecurityContext.getAuthenticationUser(), false);

        /*
            In the view we need the pools, which the user is ACTIVELY assigned to,
            in order to check whether he can edit/delete the records or not.
         */
        List<Pool> activePools = userPools
                .stream()
                .filter(UserPool::getIsActive)
                .map(UserPool::getPool)
                .collect(Collectors.toList());

        // Find all records according to the filter and the currently logged in user
        List<Record> records = recordService.getAllRecordsByFilter(filter, SecurityContext.getAuthenticationUser());

        // Add ALL assignments, the activePools, the filter, the records and the total duration of the records to the model.
        model.addAttribute("userPools", userPools);
        model.addAttribute("activePools", activePools);
        model.addAttribute("filter", filter);
        model.addAttribute("records", records);
        model.addAttribute("totalDuration", recordService.calculateDuration(records));

        return "records/overview";
    }

    /**
     * Method for handling the submission of the export button on the "analysis" page.
     *
     * @param filter contains criteria set by the user on the overview page.
     * @return path to template
     */
    @GetMapping(params = "export", value = "/overview/filter")
    public ResponseEntity<InputStreamResource> getExcelRecordsReport(@ModelAttribute PoolDateFilter filter) {
        // Create an excel sheet according to the filter and the currently logged in user
        ByteArrayInputStream in = excelGenerator.createExcelSheet(filter, SecurityContext.getAuthenticationUser());

        // Set the filename
        String filename = "Arbeitsstunden" + filter.getFrom().toString() + "bis" + filter.getTo().toString();

        // Set the Http header
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=\"" + filename + ".xlsx" + "\"");

        return ResponseEntity.ok().headers(headers).body(new InputStreamResource(in));
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
        // Get the currently logged in user
        User authenticationUser = SecurityContext.getAuthenticationUser();

        // Check recordId + permission and get record object.
        Record record = getRecord(id, authenticationUser);

        // Add all ACTIVE assignments and the record to the model
        model.addAttribute("userPools", userPoolService.findAllUserPools(authenticationUser, true));
        model.addAttribute("record", record);

        return "records/edit";
    }

    /**
     * Method for handling the submission of the "edit record" form.
     *
     * @param updatedRecord the record with updated fields
     * @param bindingResult contains possible form errors
     * @return path to the template
     */
    @PutMapping("/edit")
    public String putEditRecordPage(@Valid Record updatedRecord, BindingResult bindingResult) {
        // Check whether the record is valid
        checkRecord(updatedRecord, bindingResult);

        /* If the form contains errors, the record won't be edited and the form is displayed again with
           corresponding error messages. */
        if (bindingResult.hasErrors()) {
            return "records/edit";
        }

        // Update the record in the database
        recordService.updateRecord(updatedRecord);

        // If the record was updated successfully, redirect to the records' overview page.
        return "redirect:/records/overview/?successful=true&mode=edit";
    }

    /**
     * Method for displaying the delete form of a record determined by its id.
     *
     * @param id a record's id as specified in the path
     * @param model The Spring model
     * @return path to the template
     * @throws Exception throws exception
     */
    @GetMapping("/delete/{id}")
    public String getDeleteRecordPage(@PathVariable("id") Long id, Model model) throws Exception{
        // Check recordId + permission and get record object.
        Record record = getRecord(id, SecurityContext.getAuthenticationUser());

        // Add the record to the model
        model.addAttribute("record", record);

        return "records/delete";
    }

    /**
     * Method for handling the submission of the "delete record" form.
     * Note: The record is not deleted, but flagged as "deleted" in the database.
     *
     * @param record the record which should be deleted
     * @return path to the template
     */
    @DeleteMapping("/delete")
    public String deleteRecordPage(@ModelAttribute Record record) {
        // "Delete" the record from the database.
        recordService.deleteRecord(record);

        // If the record was "deleted" successfully, redirect to the records' overview page.
        return "redirect:/records/overview/?successful=true&mode=delete";
    }

    /**
     * This method checks a record if it contains valid and allowed times. If they're not valid or not allowed,
     * the BindingResult will be manipulated so that it contains the corresponding error messages.
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

    /**
     * This method uses the id of a record and a user object to check whether
     * the record id exists and whether the user was the creator of the record.
     * If both is true, a plain record object is returned.
     *
     * @param recordId the id of the record which should be checked
     * @param user     the user whose creatorship of the record should be checked
     * @return a plain record object which corresponds to the recordId
     * @throws NotFoundException     if no record with the specified id could be found in the database
     * @throws AccessDeniedException if the currently logged in user is not entitled to edit/delete ths record
     */
    private Record getRecord(Long recordId, User user) throws NotFoundException, AccessDeniedException {
        // Try to get the record specified by its id from the database.
        Optional<Record> optionalRecord = recordService.findRecordById(recordId);

        // Check whether a record with the id could be found.
        if (!optionalRecord.isPresent()) {
            throw new NotFoundException("No record found with ID: " + recordId);
        }

        // Check whether the current user is allowed to edit/delete this record.
        if (!optionalRecord.get().getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("The current user/editor and the record's creator are not identical.");
        }

        return optionalRecord.get();
    }
}

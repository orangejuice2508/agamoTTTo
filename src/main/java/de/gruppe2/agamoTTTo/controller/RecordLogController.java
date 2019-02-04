package de.gruppe2.agamoTTTo.controller;

import de.gruppe2.agamoTTTo.domain.bo.filter.PoolDateFilter;
import de.gruppe2.agamoTTTo.domain.entity.User;
import de.gruppe2.agamoTTTo.domain.entity.UserPool;
import de.gruppe2.agamoTTTo.security.Permission;
import de.gruppe2.agamoTTTo.security.Role;
import de.gruppe2.agamoTTTo.security.SecurityContext;
import de.gruppe2.agamoTTTo.service.PoolService;
import de.gruppe2.agamoTTTo.service.RecordLogService;
import de.gruppe2.agamoTTTo.service.UserPoolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.stream.Collectors;

/**
 * This controller is used for mapping all requests to /logs/ to concrete HTML pages in resources/templates/logs
 */
@Controller
@RequestMapping("logs")
public class RecordLogController {

    private PoolService poolService;

    private UserPoolService userPoolService;

    private RecordLogService recordLogService;

    @Autowired
    public RecordLogController(PoolService poolService, RecordLogService recordLogService, UserPoolService userPoolService) {
        this.poolService = poolService;
        this.recordLogService = recordLogService;
        this.userPoolService = userPoolService;
    }

    /**
     * Method for displaying the "overview" page for the log file.
     *
     * @param model the Spring Model
     * @return path to template
     */
    @PreAuthorize(Permission.VORGESETZTER)
    @GetMapping("/overview")
    public String getOverviewLogsPage(Model model){
        // Add an empty poolDateFilter to the model
        model.addAttribute("filter", new PoolDateFilter());

        return "logs/overview";
    }

    /**
     * Method for handling the submission of the filter on the "overview" page.
     *
     * @param filter contains criteria set by the user on the overview page
     * @param model the Spring Model
     * @return path to template
     */
    @GetMapping("overview/filter")
    public String getOverviewLogsFilterResults(@ModelAttribute PoolDateFilter filter, Model model) {
        // Update filter so that potentially wrong dates are corrected
        filter = new PoolDateFilter(filter);

        // Add the poolDateFilter and the recordLogs according to this filter to the model
        model.addAttribute("filter", filter);
        model.addAttribute("recordLogs", recordLogService.getAllRecordLogsByFilter(filter));

        return "logs/overview";
    }

    /**
     * This method adds UserPool objects to the model of every request to this controller.
     * Note: The HTML fragment of the filter requires UserPool objects and no plain pool objects.
     * This is why we have to create artificial UserPool objects, if the user is an admin.
     *
     * @param model the Spring Model
     */
    @ModelAttribute
    public void addUserPoolsToModel(Model model) {
        // Get the currently logged in user.
        User authenticationUser = SecurityContext.getAuthenticationUser();

        // The admin can see all pools, supervisors only the pools they're assigned to.
        if (authenticationUser.getRole().getRoleName().equals(Role.ADMINISTRATOR)) {
            // Return all existing pools as UserPool objects, since the HTML fragment of the filter requires that.
            List<UserPool> userPools = poolService.findAllPools()
                    .stream()
                    .map(UserPool::new)
                    .collect(Collectors.toList());
            // Add all pools as userPool objects to the model.
            model.addAttribute("userPools", userPools);
        } else {
            // Add the active assignments to the model.
            model.addAttribute("userPools", userPoolService.findAllUserPools(authenticationUser, true));
        }
    }

}

package de.gruppe2.agamoTTTo.controller;

import de.gruppe2.agamoTTTo.domain.entity.User;
import de.gruppe2.agamoTTTo.security.Permission;
import de.gruppe2.agamoTTTo.security.SessionUtils;
import de.gruppe2.agamoTTTo.service.RoleService;
import de.gruppe2.agamoTTTo.service.UserService;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * This controller is used for mapping all requests to /employees/ to concrete HTML pages in resources/templates/employees
 */
@Controller
@RequestMapping("employees")
public class EmployeeController extends BaseController {

    private UserService userService;

    private MessageSource messageSource;

    private RoleService roleService;

    private SessionUtils sessionUtils;

    @Autowired
    public EmployeeController(UserService userService,
                              MessageSource messageSource,
                              RoleService roleService,
                              SessionUtils sessionUtils) {
        this.userService = userService;
        this.messageSource = messageSource;
        this.roleService = roleService;
        this.sessionUtils = sessionUtils;
    }

    /**
     * Method for displaying the "add new employee" page.
     *
     * @param model the Spring Model
     * @return path to template
     */
    @PreAuthorize(Permission.VORGESETZTER)
    @GetMapping("/add")
    public String getAddEmployeesPage(Model model){
        // Add a new user i.e. a new employee to the model
        model.addAttribute("user", new User());

        return "employees/add";
    }

    /**
     * Method for handling the submission of the "add new employee" form.
     *
     * @param user the user as obtained from the form
     * @param bindingResult contains possible form errors
     * @return path to resulting template
     */
    @PostMapping("/add")
    public String postAddEmployeesPage(@ModelAttribute @Valid User user, BindingResult bindingResult){
        /* If the form contains errors, the new employee/user won't be added and the form is displayed again with
           corresponding error messages. */
        if(bindingResult.hasErrors()){
            return "employees/add";
        }

        /* Try to add the employee/user to the database. If the email address is already registered, a DataIntegrityViolation
            will be thrown by the UserService/UserRepository. Then the form is shown again with a corresponding
            error message.
         */
        try {
            userService.addUser(user);
        } catch (DataIntegrityViolationException e) {
            bindingResult.rejectValue("email", "error.user", messageSource.getMessage("employees.error.email_not_unique", null, Locale.getDefault()));
            return "employees/add";
        }

        // If the user was added successfully, redirect to the "add page" with an empty form.
        return "redirect:/employees/add/?successful=true";
    }

    /**
     * Method for displaying the "overview employees" page.
     *
     * @return path to template
     */
    @PreAuthorize(Permission.ADMINISTRATOR)
    @GetMapping(value = "/overview")
    public String getOverviewEmployeesPage() {
        return "employees/overview";
    }

    /**
     * Method for displaying the results of the retrieved employees.
     * Either all employees or only those according to a search term are returned.
     *
     * @param type the type of action chosen by the user: either "show all" employees or "search" employees, stored as a parameter in the url
     * @param searchTerm the entered search term, stored as a parameter in the url
     * @param model the Spring model
     * @return path to template
     */
    @GetMapping(value = "/overview", params = {"type", "searchTerm"})
    public String getOverviewEmployeesSearchResults(
            @RequestParam(value = "type") String type,
            @RequestParam("searchTerm") String searchTerm,
            Model model) {

        // Initialize empty list
        List<User> users = Collections.emptyList();

        // Determine type of action chosen by the user
        if (type.equals("showAll")) {
            // Find all users and empty the search term.
            users = userService.findAllUsers();
            searchTerm = "";
        } else if (type.equals("search")) {
            // Find users according to search term
            users = userService.findUsersBySearchTerm(searchTerm);
        }

        // Add the retrieved users/employees and the searchTerm to the model
        model.addAttribute("users", users);
        model.addAttribute("serchTerm", searchTerm);

        return "employees/overview";
    }

    /**
     * Method for displaying the edit form of an employee determined by its id.
     *
     * @param id a user's/employee's id as specified in the path
     * @param model The Spring model
     * @return path to the template
     */
    @GetMapping("/edit/{id}")
    public String getEditEmployeePage(@PathVariable("id") Long id, Model model) throws Exception {
        // Try to get the user/employee specified by its id from the database.
        Optional<User> optionalUser = userService.findUserById(id);

        // Check whether a user/employee with the id could be found.
        if (!optionalUser.isPresent()) {
            throw new NotFoundException("No employee found with ID: " + id);
        }

        // Add the user/employee and new possible roles for this user/employee to the model.
        model.addAttribute("user", optionalUser.get());
        model.addAttribute("roles", roleService.findPossibleRoles(optionalUser.get()));

        return "employees/edit";
    }

    /**
     * Method for handling the submission of the "edit employee" form.
     *
     * @param updatedUser the user/employee with updated fields
     * @param bindingResult contains possible form errors
     * @return path to the template
     */
    @PutMapping("/edit")
    public String putEditEmployeePage(@Valid User updatedUser, BindingResult bindingResult, Model model) {

        /* If the form contains errors, the user won't be updated and the form is displayed again with
        corresponding error messages. */
        if (bindingResult.hasErrors()) {
            return "employees/edit";
        }

        /* Try to update the user/employee in the database. If the email address is already registered, a DataIntegrityViolation
        will be thrown by the UserService/UserRepository. Then the form is shown again with a corresponding
        error message.
        */
        try {
            userService.updateUser(updatedUser);
            // If the user was updated successfully, expire all his sessions so that he has to log in again.
            sessionUtils.expireUserSessions(updatedUser.getId());
        } catch (DataIntegrityViolationException e) {
            // Add new possible roles for this user/employee to the model.
            model.addAttribute("roles", roleService.findPossibleRoles(updatedUser));
            bindingResult.rejectValue("email", "error.user", messageSource.getMessage("employees.error.email_not_unique", null, Locale.getDefault()));
            return "employees/edit";
        }

        // If the user was edited successfully, redirect to the overview page.
        return "redirect:/employees/overview/?successful=true";
    }
}

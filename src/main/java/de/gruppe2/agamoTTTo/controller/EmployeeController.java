package de.gruppe2.agamoTTTo.controller;

import de.gruppe2.agamoTTTo.domain.entity.User;
import de.gruppe2.agamoTTTo.security.Permission;
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
import java.util.Locale;
import java.util.Optional;

@Controller
@RequestMapping("employees")
public class EmployeeController extends BaseController {

    private UserService userService;

    private MessageSource messageSource;

    private RoleService roleService;

    @Autowired
    public EmployeeController(UserService userService, MessageSource messageSource, RoleService roleService) {
        this.userService = userService;
        this.messageSource = messageSource;
        this.roleService = roleService;
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
        /* If the form contains errors, the new employee won't be added and the form is displayed again with
           corresponding error messages. */
        if(bindingResult.hasErrors()){
            return "employees/add";
        }

        /* Try to add the user to the database. If the email address is already registered, a DataIntegrityViolation
            will be thrown by the UserService/UserRepository. Then the form is shown again with a corresponding
            error message.
         */
        try {
            userService.addUser(user);
        } catch (DataIntegrityViolationException e) {
            bindingResult.rejectValue("email", "error.user", messageSource.getMessage("employees.error.email_not_unique", null, Locale.getDefault()));
            return "employees/add";
        }

        // If the user was added successfully, reload the page with an empty form.
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
     * Method for displaying the search results of the retrieved employees.
     *
     * @param searchTerm the entered search, stored as a parameter in the url
     * @param model      The Spring model
     * @return path to template
     */
    @GetMapping(value = "/overview", params = "searchTerm")
    public String getOverviewEmployeesSearchResults(@RequestParam("searchTerm") String searchTerm, Model model) {
        model.addAttribute("users", userService.findUsersBySearchTerm(searchTerm));
        model.addAttribute("serchTerm", searchTerm);
        return "employees/overview";
    }

    /**
     * Method for displaying the edit form of an employee determined by its id.
     *
     * @param id    a user's id as specified in the path
     * @param model The Spring model
     * @return path to the template
     */
    @GetMapping("/edit/{id}")
    public String getEditEmployeePage(@PathVariable("id") Long id, Model model) throws Exception {

        Optional<User> optionalUser = userService.findUserById(id);

        // Check whether a record with the id could be found.
        if (!optionalUser.isPresent()) {
            throw new NotFoundException("No record found with ID: " + id);
        }

        model.addAttribute("user", optionalUser.get());
        model.addAttribute("roles", roleService.findAllRoles());

        return "employees/edit";
    }

    /**
     * Method for handling the submission of the "edit employee" form.
     *
     * @param updatedUser   the user with updated fields
     * @param bindingResult contains possible form errors
     * @return path to the template
     */
    @PutMapping("/edit/{id}")
    public String putEditEmployeePage(@Valid User updatedUser, BindingResult bindingResult, Model model) {

        /* If the form contains errors, the user won't be updated and the form is displayed again with
        corresponding error messages. */
        if (bindingResult.hasErrors()) {
            return "employees/edit";
        }

        /* Try to update the user in the database. If the email address is already registered, a DataIntegrityViolation
        will be thrown by the UserService/UserRepository. Then the form is shown again with a corresponding
        error message.
        */
        try {
            userService.updateUser(updatedUser);
        } catch (DataIntegrityViolationException e) {
            model.addAttribute("roles", roleService.findAllRoles());
            bindingResult.rejectValue("email", "error.user", messageSource.getMessage("employees.error.email_not_unique", null, Locale.getDefault()));
            return "employees/edit";
        }

        // If the user was edited successfully, reload the overview page.
        return "redirect:/employees/overview/?successful=true";
    }
}

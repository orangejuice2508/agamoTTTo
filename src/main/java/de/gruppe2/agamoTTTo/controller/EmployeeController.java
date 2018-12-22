package de.gruppe2.agamoTTTo.controller;

import de.gruppe2.agamoTTTo.domain.entity.User;
import de.gruppe2.agamoTTTo.security.Permission;
import de.gruppe2.agamoTTTo.service.UserService;
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
@RequestMapping("employees")
public class EmployeeController extends BaseController {

    private UserService userService;

    private MessageSource messageSource;

    @Autowired
    public EmployeeController(UserService userService, MessageSource messageSource) {
        this.userService = userService;
        this.messageSource = messageSource;
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
        else{
            /* Try to add the user to the database. If the email address is already registered, a DataIntegrityViolation
                will be thrown by the UserService/UserRepository. Then the form is shown again with a corresponding
                error message.
             */
            try{
                userService.addUser(user);
            }
            catch(DataIntegrityViolationException e){
                bindingResult.rejectValue("email", "error.user", messageSource.getMessage("employees.add.error.email_not_unique", null, Locale.getDefault()));
                return "employees/add";
            }
        }

        // If the user was added successfully, reload the page with an empty form.
        return "redirect:/employees/add/?successful=true";
    }

    @GetMapping(value = "/overview")
    public String getOverviewEmployeesPage() {
        return "employees/overview";
    }

    @GetMapping(value = "/overview", params = "searchTerm")
    public String getOverviewEmployeesAfterSearchPage(@RequestParam("searchTerm") String searchTerm, Model model) {
        model.addAttribute("users", userService.searchForUser(searchTerm));
        return "employees/overview";
    }

}

package de.gruppe2.agamoTTTo.controller;

import de.gruppe2.agamoTTTo.entity.User;
import de.gruppe2.agamoTTTo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.util.Locale;

@Controller
@RequestMapping("employees")
public class EmployeesController {

    private UserService userService;

    private MessageSource messageSource;

    @Autowired
    public EmployeesController(UserService userService, MessageSource messageSource) {
        this.userService = userService;
        this.messageSource = messageSource;
    }

    @GetMapping("/add")
    public String getAddEmployeesPage(Model model){
        model.addAttribute("user", new User());
        return "employees/add";
    }

    @PostMapping("/add")
    public String postAddEmployeesPage(@ModelAttribute @Valid User user, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            return "employees/add";
        }
        else{
            try{
                userService.addUser(user);
            }
            catch(DataIntegrityViolationException e){
                bindingResult.rejectValue("email", "error.user", messageSource.getMessage("employees.add.error.email_not_unique", null, Locale.getDefault()));
                return "employees/add";
            }
        }

        return "redirect:/employees/add/?successful=true";
    }
}

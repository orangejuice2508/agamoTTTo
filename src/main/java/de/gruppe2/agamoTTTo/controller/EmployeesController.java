package de.gruppe2.agamoTTTo.controller;

import de.gruppe2.agamoTTTo.entity.User;
import de.gruppe2.agamoTTTo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

@Controller
@RequestMapping("employees")
public class EmployeesController {

    private UserService userService;

    @Autowired
    public EmployeesController(UserService userService) {
        this.userService = userService;
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
        
        userService.addUser(user);

        return "redirect:/employees/add/?successful=true";
    }
}

package de.gruppe2.agamoTTTo.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
public class MainController {
    @RequestMapping("/")
    public String indexPage(){
        return "index";
    }

    @GetMapping("/home")
    public String userInfo(Model model, Principal principal) {
        // After user logged in successfully.
        String email = principal.getName();

        model.addAttribute("email", email);

        User loggedInUser = (User) ((Authentication) principal).getPrincipal();

        String authorities = loggedInUser.getAuthorities().toString();
        model.addAttribute("authorities", authorities);

        return "home";
    }

    @GetMapping("accessDenied")
    public String accessDenied(Model model, Principal principal) {
        return "access_denied";
    }
}

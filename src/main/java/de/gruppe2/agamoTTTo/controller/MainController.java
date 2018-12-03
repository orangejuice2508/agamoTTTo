package de.gruppe2.agamoTTTo.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.security.Principal;

@Controller
public class MainController {
    @GetMapping("/")
    public String indexPage(Principal principal) {
        // If a user is logged in, then redirect them to the "home"-page.
        if(principal != null){
            return "redirect:/home";
        }

        return "index";
    }

    @GetMapping("/home")
    public String userInfo(Model model, Principal principal) {
        // After user logged in successfully.
        String email = principal.getName();

        model.addAttribute("email", email);

        User loggedInUser = (User) ((Authentication) principal).getPrincipal();

        String authority = loggedInUser.getAuthorities().toString();
        model.addAttribute("authority", authority);

        return "home";
    }

    @GetMapping("/accessDenied")
    public String accessDenied() {
        return "access-denied";
    }
}

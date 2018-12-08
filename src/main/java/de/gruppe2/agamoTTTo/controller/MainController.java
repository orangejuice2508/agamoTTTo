package de.gruppe2.agamoTTTo.controller;

import de.gruppe2.agamoTTTo.security.CustomSecurityUser;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.security.Principal;

@Controller
public class MainController {

    /**
     * Method for displaying the index page.
     *
     * @param principal the currently logged in user, null if he is not logged in
     * @return path to template
     */
    @GetMapping("/")
    public String indexPage(Principal principal) {
        // If a user is logged in, then redirect them to the "home"-page.
        if(principal != null){
            return "redirect:/home";
        }

        return "index";
    }

    /**
     * Method for displaying the "home" page.
     *
     * @param model the Spring Model
     * @param principal the currently logged in user, null if he is not logged in
     * @return path to template
     */
    @GetMapping("/home")
    public String userInfo(Model model, Principal principal) {
        // After user logged in successfully.
        String email = principal.getName();

        model.addAttribute("email", email);

        CustomSecurityUser loggedInUser = (CustomSecurityUser) ((Authentication) principal).getPrincipal();

        String authority = loggedInUser.getAuthorities().toString();
        model.addAttribute("authority", authority);

        return "home";
    }

    /**
     * Method for displaying the "access denied" page. When the user is logged in as ROLE_X,
     * but wants to access a page that requires ROLE_Y, the exception handler of Spring's HTTPSecurity
     * will redirect to this page.
     *
     * @return path to template
     */
    @GetMapping("/accessDenied")
    public String accessDenied() {
        return "access-denied";
    }
}

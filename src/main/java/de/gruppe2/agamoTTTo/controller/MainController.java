package de.gruppe2.agamoTTTo.controller;

import de.gruppe2.agamoTTTo.domain.bo.forms.NewPasswordForm;
import de.gruppe2.agamoTTTo.domain.entity.User;
import de.gruppe2.agamoTTTo.security.Permission;
import de.gruppe2.agamoTTTo.security.SecurityContext;
import de.gruppe2.agamoTTTo.service.SecurityService;
import de.gruppe2.agamoTTTo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.security.Principal;
import java.util.Locale;

/**
 * This controller is used for mapping all requests to / to concrete HTML pages in resources/templates/
 */
@Controller
public class MainController {

    private UserService userService;

    private SecurityService securityService;

    private MessageSource messageSource;

    @Autowired
    public MainController(UserService userService, MessageSource messageSource, SecurityService securityService) {
        this.userService = userService;
        this.messageSource = messageSource;
        this.securityService = securityService;
    }

    /**
     * Method for displaying the index page.
     *
     * @param principal the currently logged in user, null if he is not logged in
     * @return path to template
     */
    @GetMapping("/")
    public String getIndexPage(Principal principal) {
        // If a user is logged in, then redirect them to the "home"-page.
        if(principal != null){
            return "redirect:/home";
        }

        return "index";
    }

    /**
     * Method for displaying the "home" page.
     *
     * @return path to template
     */
    @PreAuthorize(Permission.IS_AUTHENTICATED)
    @GetMapping("/home")
    public String getHomePage() {
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
    public String getAccessDeniedPage() {
        return "access_denied";
    }

    /**
     * Method for displaying the "forgot password" page.
     *
     * @param model the Spring Model
     * @return path to template
     */
    @GetMapping("/forgotPassword")
    public String getForgotPasswordPage(Model model) {
        // Add an empty string for the password to the model
        model.addAttribute("email", "");

        return "forgot_password";
    }

    /**
     * Method for submitting the "forgot password" page.
     *
     * @param email the entered email addresss
     * @return path to template
     */
    @PostMapping("/forgotPassword")
    public String postForgotPasswordPage(@RequestParam("email") String email, HttpServletRequest request) {
        try {
            User user = (User) userService.loadUserByUsername(email);
            userService.sendPasswordResetEmail(user);

            return "redirect:/forgotPassword?submitted=true";
        } catch (UsernameNotFoundException e) {
            return "redirect:/forgotPassword?submitted=true";
        }
    }

    /**
     * This method uses the SecurityService to check whether a token is valid and then redirects
     * the user according to the result.
     *
     * @param userId the user id as specified in the url
     * @param token  the token as specified in the url
     * @return path to template
     */
    @GetMapping("/checkToken")
    public String checkToken(@RequestParam("userId") Long userId, @RequestParam("token") String token) {

        // Check whether the token is valid
        Boolean isPasswordResetTokenValid = securityService.isPasswordResetTokenValid(userId, token);

        // If it is NOT valid, redirect to the index page
        if (!isPasswordResetTokenValid) {
            return "redirect:/forgotPassword?token=invalid";
        }

        // If it is valid, redirect to the updatePassword page
        return "redirect:/updatePassword";
    }


    /**
     * Method for displaying the "update password" page.
     *
     * @param model the Spring Model
     * @return path to template
     */
    @PreAuthorize("hasAuthority('UPDATE_PASSWORD_PRIVILEGE')")
    @GetMapping("/updatePassword")
    public String getUpdatePasswordPage(Model model) {

        model.addAttribute("newPasswordForm", new NewPasswordForm());

        return "update_password";
    }

    /**
     * Method for handling the submission of the "update password" form.
     *
     * @param newPasswordForm contains the new password in plain text
     * @param bindingResult contains possible form errors
     * @param request provides request information for HTTP servlets
     * @return path to template
     */
    @PreAuthorize("hasAuthority('UPDATE_PASSWORD_PRIVILEGE')")
    @PostMapping("/updatePassword")
    public String postSettingsPage(@Valid NewPasswordForm newPasswordForm, BindingResult bindingResult, HttpServletRequest request) {

        // Check the entered passwords for validity
        checkEnteredPasswords(newPasswordForm, bindingResult);

        /* If the form contains errors, the passwords won't be changed and the form is displayed again with
        corresponding error messages. */
        if (bindingResult.hasErrors()) {
            return "update_password";
        }

        // Try to change the password in the database
        userService.changePassword(SecurityContext.getAuthenticationUser(), newPasswordForm.getNewPassword());

        // Logout the current user and prompt them to log in again.
        new SecurityContextLogoutHandler().logout(request, null, null);

        return "redirect:/?updatePassword=successful";
    }

    /**
     * This method checks the newPasswordForm if it contains valid passwords. If it's not valid, the BindingResult will
     * be manipulated so that it contains the corresponding error messages.
     *
     * @param newPasswordForm contains the new password in plain text
     * @param bindingResult   contains possible form errors
     */
    private void checkEnteredPasswords(NewPasswordForm newPasswordForm, BindingResult bindingResult) {

        // The new password must match the confirmation password
        if (!newPasswordForm.getNewPassword().equals(newPasswordForm.getConfirmationPassword())) {
            String errorMessage = messageSource.getMessage("settings.error.password_mismatch", null, Locale.getDefault());
            bindingResult.rejectValue("newPassword", "error.changePasswordForm", errorMessage);
            bindingResult.rejectValue("confirmationPassword", "error.changePasswordForm", errorMessage);
        }
    }
}

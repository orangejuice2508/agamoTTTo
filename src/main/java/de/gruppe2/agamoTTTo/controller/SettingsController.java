package de.gruppe2.agamoTTTo.controller;

import de.gruppe2.agamoTTTo.domain.base.ChangePasswordForm;
import de.gruppe2.agamoTTTo.domain.entity.User;
import de.gruppe2.agamoTTTo.security.Permission;
import de.gruppe2.agamoTTTo.security.SecurityContext;
import de.gruppe2.agamoTTTo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Locale;

@Controller
@RequestMapping("settings")
public class SettingsController {

    private UserService userService;

    private MessageSource messageSource;

    @Autowired
    public SettingsController(MessageSource messageSource, UserService userService) {
        this.messageSource = messageSource;
        this.userService = userService;
    }

    /**
     * Method for displaying the "change password" page.
     *
     * @param model the Spring Model
     * @return path to template
     */
    @GetMapping("/changePassword")
    @PreAuthorize(Permission.IS_AUTHENTICATED)
    public String getSettingsPage(Model model) {

        model.addAttribute("changePasswordForm", new ChangePasswordForm());

        return "settings/change_password";
    }

    /**
     * Method for handling the submission of the "change password" form.
     *
     * @param changePasswordForm contains the old and new password in plain text
     * @param bindingResult      contains possible form errors
     * @param request            provides request information for HTTP servlets
     * @return path to template
     */
    @PostMapping("/changePassword")
    public String postSettingsPage(@Valid ChangePasswordForm changePasswordForm, BindingResult bindingResult, HttpServletRequest request) {

        User authenticationUser = SecurityContext.getAuthenticationUser();

        checkEnteredPasswords(authenticationUser, changePasswordForm, bindingResult);

        if (bindingResult.hasErrors()) {
            return "settings/change_password";
        }

        userService.changePassword(authenticationUser, changePasswordForm.getNewPassword());

        // Logout the current user and prompt them to log in again.
        new SecurityContextLogoutHandler().logout(request, null, null);
        return "redirect:/?changePassword=successful";
    }

    /**
     * This method checks the changePasswordForm if it contains valid passwords. If it's not valid, the BindingResult will
     * be manipulated so that it contains the corresponding error messages.
     *
     * @param user               the user whose passwords should be checked
     * @param changePasswordForm contains the old and new password in plain text
     * @param bindingResult      contains possible form errors
     */
    private void checkEnteredPasswords(User user, ChangePasswordForm changePasswordForm, BindingResult bindingResult) {

        // The new password must equal the confirmation password
        if (!changePasswordForm.getNewPassword().equals(changePasswordForm.getConfirmationPassword())) {
            String errorMessage = messageSource.getMessage("settings.error.password_mismatch", null, Locale.getDefault());
            bindingResult.rejectValue("newPassword", "error.changePasswordForm", errorMessage);
            bindingResult.rejectValue("confirmationPassword", "error.changePasswordForm", errorMessage);
        }

        // The entered old password must equal the old password in the database
        if (!userService.isOldPasswordCorrect(user, changePasswordForm.getOldPassword())) {
            String errorMessage = messageSource.getMessage("settings.error.old_password_incorrect", null, Locale.getDefault());
            bindingResult.rejectValue("oldPassword", "error.changePasswordForm", errorMessage);
        }
    }
}

package de.gruppe2.agamoTTTo.domain.bo.forms;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

/**
 * This class is used for changing a user's password
 */
@Getter
@Setter
public class NewPasswordForm {
    @NotEmpty
    @Size(min = 8, max = 20)
    private String newPassword;

    @NotEmpty
    @Size(min = 8, max = 20)
    private String confirmationPassword;
}

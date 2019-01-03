package de.gruppe2.agamoTTTo.domain.base;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Getter
@Setter
public class ChangePasswordForm {

    @NotEmpty
    @Size(min = 10, max = 20)
    private String oldPassword;

    @NotEmpty
    @Size(min = 10, max = 20)
    private String newPassword;

    @NotEmpty
    @Size(min = 10, max = 20)
    private String confirmationPassword;

}

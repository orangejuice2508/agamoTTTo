package de.gruppe2.agamoTTTo.domain.bo.forms;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Getter
@Setter
public class ChangePasswordForm extends NewPasswordForm {

    @NotEmpty
    @Size(min = 8, max = 20)
    private String oldPassword;

}

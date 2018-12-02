package de.gruppe2.agamoTTTo.entity;

import de.gruppe2.agamoTTTo.security.SecurityRole;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@Entity
@Table(name = "agamottto_user")
public class User {
    @Id
    @GeneratedValue
    @Column(name = "user_id")
    private Long id;

    @Size(max = 60)
    @NotEmpty
    @Column(name = "e_mail", unique = true)
    @Email
    private String email;

    @Size(min=1, max = 60)
    @NotEmpty
    @Column(name = "first_name")
    private String firstName;

    @Size(min=1, max = 60)
    @NotEmpty
    @Column(name = "last_name")
    private String lastName;

    @Size(max = 120)
    @NotNull
    @Column(name = "encrypted_password")
    private String encryptedPassword;

    @NotNull
    @Column(name = "enabled")
    private Boolean enabled;

    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    public User() {
        this.encryptedPassword = "";
        this.enabled = Boolean.TRUE;
        this.role = new Role(3L, SecurityRole.MITARBEITER);
    }
}

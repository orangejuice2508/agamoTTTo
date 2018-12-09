package de.gruppe2.agamoTTTo.domain.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Set;

@Getter
@Setter
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

    @ManyToMany
    @JoinTable(
            name = "user_pool",
            joinColumns = {@JoinColumn(name = "user_id")},
            inverseJoinColumns = {@JoinColumn(name = "pool_id")})
    private Set<Pool> pools;

    public User() {
        this.encryptedPassword = "";
        this.enabled = Boolean.TRUE;
        this.role = new Role(3L, de.gruppe2.agamoTTTo.security.Role.MITARBEITER);
    }

    public User (User user){
        this.id = user.getId();
        this.email = user.getEmail();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.encryptedPassword = user.getEncryptedPassword();
        this.enabled = user.getEnabled();
        this.role = user.getRole();
    }
}
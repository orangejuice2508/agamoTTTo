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

    @NotEmpty
    @Size(max = 60)
    @Column(name = "e_mail", unique = true)
    @Email
    private String email;

    @NotEmpty
    @Size(min = 1, max = 60)
    @Column(name = "first_name")
    private String firstName;

    @NotEmpty
    @Size(min = 1, max = 60)
    @Column(name = "last_name")
    private String lastName;

    @NotNull
    @Size(max = 120)
    @Column(name = "encrypted_password")
    private String encryptedPassword;

    @NotNull
    @Column(name = "enabled")
    private Boolean enabled;

    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_pool",
            joinColumns = {@JoinColumn(name = "user_id")},
            inverseJoinColumns = {@JoinColumn(name = "pool_id")})
    private Set<Pool> pools;

    @OneToMany(mappedBy = "user")
    private Set<Record> records;

    public User() {
        this.encryptedPassword = "";
        this.enabled = Boolean.TRUE;
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
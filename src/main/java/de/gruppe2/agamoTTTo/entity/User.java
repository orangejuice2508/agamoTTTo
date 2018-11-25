package de.gruppe2.agamoTTTo.entity;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name = "USER")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "USER_ID")
    private Long id;

    @NotNull
    @Column(name = "E_MAIL")
    private String email;

    @NotNull
    @Column(name="FIRST_NAME")
    private String firstName;

    @NotNull
    @Column(name="LAST_NAME")
    private String lastName;

    @NotNull
    @Column(name = "ENCRYPTED_PASSWORD")
    private String encryptedPassword;

    @NotNull
    @Column(name = "ENABLED")
    private Boolean enabled;

    @ManyToMany(cascade = {CascadeType.ALL})
    @JoinTable(name = "USER_ROLE", joinColumns = {@JoinColumn(name = "USER_ID")}, inverseJoinColumns = {@JoinColumn(name = "ROLE_ID")})
    private Set<Role> roles = new HashSet<>();
}

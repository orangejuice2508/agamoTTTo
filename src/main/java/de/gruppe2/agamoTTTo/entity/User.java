package de.gruppe2.agamoTTTo.entity;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@Entity
@Table(name = "agamottto_user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "user_id")
    private Long id;

    @Size(max = 60)
    @NotNull
    @Column(name = "e_mail")
    private String email;

    @Size(max = 60)
    @NotNull
    @Column(name = "first_name")
    private String firstName;

    @Size(max = 60)
    @NotNull
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
}
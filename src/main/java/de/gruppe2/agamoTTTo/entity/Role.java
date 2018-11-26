package de.gruppe2.agamoTTTo.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "role")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ROLE_ID")
    private Long id;

    @Column(name = "ROLE_NAME")
    private String roleName;

    // ToDo: Get mapping right.
    /*@OneToMany(mappedBy = "role")
    private Set<User> users;*/
}

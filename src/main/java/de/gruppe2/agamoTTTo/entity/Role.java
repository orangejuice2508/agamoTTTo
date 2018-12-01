package de.gruppe2.agamoTTTo.entity;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.Size;

@Data
@Entity
@Table(name = "role")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "role_id")
    private Long id;

    @Size(max = 60)
    @Column(name = "role_name")
    private String roleName;

    // ToDo: Get mapping right.
    /*@OneToMany(mappedBy = "role")
    private Set<User> users;*/
}
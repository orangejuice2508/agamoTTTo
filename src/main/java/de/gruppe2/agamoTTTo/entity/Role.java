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

    // ToDo: Get relational mapping right or alter authorities
/*    @ManyToMany(mappedBy = "roles")
    private Set<User> users = new HashSet<>();*/
}

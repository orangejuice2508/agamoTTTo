package de.gruppe2.agamoTTTo.domain.entity;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Set;

/**
 * This class corresponds to the database table "role".
 * Its columns correspond to the attributes of this class.
 */
@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "role")
public class Role {

    @Id
    @GeneratedValue
    @Column(name = "role_id")
    private Long id;

    @NotNull
    @Size(max = 60)
    @Column(name = "role_name")
    private String roleName;

    @OneToMany(mappedBy = "role")
    private Set<User> users;

    public Role(Long id, String roleName){
        this.id = id;
        this.roleName = roleName;
    }
}
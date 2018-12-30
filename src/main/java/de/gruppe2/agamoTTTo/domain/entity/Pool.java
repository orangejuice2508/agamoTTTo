package de.gruppe2.agamoTTTo.domain.entity;


import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "pool")
@EntityListeners(AuditingEntityListener.class)
public class Pool {

    @Id
    @GeneratedValue
    @Column(name = "pool_id")
    private Long id;

    @NotEmpty
    @Size(max = 100)
    @Column(name = "pool_name", unique = true)
    private String name;

    @ManyToOne
    @JoinColumn(name = "owner", nullable = false)
    @CreatedBy
    private User owner;

    @ManyToMany(mappedBy = "pools")
    private Set<User> users;
}
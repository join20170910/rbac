package com.imooc.uaa.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Set;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "mooc_permissions")
public class Permission implements GrantedAuthority, Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    @Size(max = 50)
    @Column(name = "permission_name",unique = true,nullable = false,length = 50)
    private String authority;
    @NotNull
    @Size(max = 50)
    @Column(name = "display_name", nullable = false,length = 50)
    private String displayName;
    @JsonIgnore
    @ManyToMany(mappedBy = "permissions")
    private Set<Role> roles;

}

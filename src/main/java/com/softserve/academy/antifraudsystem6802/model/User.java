package com.softserve.academy.antifraudsystem6802.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;

@Entity
@Getter
@Setter
@Table(name = "t_user")
public class User implements UserDetails, UserDetailsMixin {
    @Id
    @GeneratedValue
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    Long id;
    @NotEmpty
    String name;
    @NotEmpty
    @Column(unique = true)
    String username;
    @NotEmpty
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    String password;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    Role role;
    @JsonIgnore
    boolean isAccountNonLocked;

    @Override
    public boolean isAccountNonLocked() {
        return isAccountNonLocked;
    }
}

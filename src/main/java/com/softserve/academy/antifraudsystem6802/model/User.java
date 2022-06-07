package com.softserve.academy.antifraudsystem6802.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotEmpty;

@Entity
@Getter
@Setter
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
}

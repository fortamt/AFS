package com.softserve.academy.antifraudsystem6802.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public interface UserDetailsMixin extends UserDetails {
    @JsonIgnore
    @Override
    default Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @JsonIgnore
    @Override
    default boolean isAccountNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    default boolean isAccountNonLocked() {
        return true;
    }

    @JsonIgnore
    @Override
    default boolean isCredentialsNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    default boolean isEnabled() {
        return true;
    }
}

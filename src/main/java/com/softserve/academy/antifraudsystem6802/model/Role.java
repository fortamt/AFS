package com.softserve.academy.antifraudsystem6802.model;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {
    MERCHANT, ADMINISTRATOR, SUPPORT;

    @Override
    public String getAuthority() {
        return "ROLE_" + name();
    }
}

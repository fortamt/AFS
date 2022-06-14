package com.softserve.academy.antifraudsystem6802.model.request;

import com.softserve.academy.antifraudsystem6802.model.Role;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoleRequest {
    private String username;
    private Role role;
}

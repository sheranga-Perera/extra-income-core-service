package com.phx.ei.common.security;

public enum Role {
    ADMIN,
    COMPANY,
    INDIVIDUAL;

    public String getAuthority() {
        return "ROLE_" + this.name();
    }
}



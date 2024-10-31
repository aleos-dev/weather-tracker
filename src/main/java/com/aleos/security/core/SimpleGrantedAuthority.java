package com.aleos.security.core;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class SimpleGrantedAuthority implements GrantedAuthority {

    private final Role role;

    public SimpleGrantedAuthority(Role role) {
        this.role = role;
    }

    @JsonCreator
    public SimpleGrantedAuthority(@JsonProperty("authority") String authority) {
        this.role = Role.valueOf(authority);
    }

    @Override
    public Role getAuthority() {
        return role;
    }

    @Override
    public String toString() {
        return role.toString();
    }
}

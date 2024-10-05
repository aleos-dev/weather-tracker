package com.aleos.security.core;

public final class SimpleGrantedAuthority implements GrantedAuthority {

    private final Role role;

    public SimpleGrantedAuthority(Role role) {
        this.role = role;
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

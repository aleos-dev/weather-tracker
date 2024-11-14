package com.aleos.security.core;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A simple implementation of the {@code GrantedAuthority} interface, representing a specific authority granted to
 * an authentication object based on user roles.
 * <p>
 * This class encapsulates a {@code Role} that defines the authority granted. It can be constructed either
 * with a {@code Role} directly or with an authority string which is mapped to a {@code Role} via {@code Role.valueOf}.
 * <p>
 * The authority is typically used to control access to different parts of an application based on the assigned role
 * of the authenticated user.
 */
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

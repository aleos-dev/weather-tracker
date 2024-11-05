package com.aleos.security.core;

import com.aleos.security.web.filters.AnonymousAuthenticationFilter;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@JsonIgnoreProperties("anonymous")
public class AuthenticationToken implements Authentication {

    @JsonProperty
    private final String principal;

    @JsonProperty
    private final List<GrantedAuthority> authorities;

    @JsonProperty
    private boolean authenticated;

    @JsonCreator
    public AuthenticationToken(
            @JsonProperty("principal") String principal,
            @JsonProperty("authorities") List<GrantedAuthority> authorities,
            @JsonProperty("authenticated") boolean authenticated) {
        this.principal = principal;
        this.authorities = authorities;
        this.authenticated = authenticated;
    }

    public AuthenticationToken(String principal, GrantedAuthority... authorities) {
        this.principal = principal;
        this.authorities = Arrays.asList(authorities);
        authenticated = true;
    }

    @Override
    public String getPrincipal() {
        return principal;
    }

    @Override
    public List<GrantedAuthority> getAuthorities() {
        return new ArrayList<>(authorities);
    }

    @Override
    public boolean setAuthenticated() {
        return authenticated;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) {
        this.authenticated = isAuthenticated;
    }

    @Override
    public boolean isAnonymous() {
        return principal.equals(AnonymousAuthenticationFilter.ANONYMOUS_USER);
    }

    @Override
    public String toString() {
        return "AuthenticationToken [principal=" + principal + ", authorities=" + authorities + ", authenticated=" + authenticated + "]";
    }
}

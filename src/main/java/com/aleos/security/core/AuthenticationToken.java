package com.aleos.security.core;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AuthenticationToken implements Authentication {

    private final String principal;

    private final String credential;

    private final List<GrantedAuthority> authorities;

    private boolean authenticated;

    public AuthenticationToken(String principal, String credential, GrantedAuthority... authorities) {
        this.principal = principal;
        this.credential = credential;
        this.authorities = Arrays.asList(authorities);
        authenticated = true;
    }

    @Override
    public String getPrincipal() {
        return principal;
    }

    @Override
    public String getCredential() {
        return credential;
    }

    @Override
    public List<GrantedAuthority> getAuthorities() {
        return new ArrayList<>(authorities);
    }

    @Override
    public boolean isAuthenticated() {
        return authenticated;
    }

    @Override
    public void isAuthenticated(boolean isAuthenticated) {
        this.authenticated = isAuthenticated;
    }
}

package com.aleos.security.core;

import java.util.List;

public interface Authentication {

    String getPrincipal();

    String getCredential();

    List<GrantedAuthority> getAuthorities();

    boolean isAuthenticated();

    void isAuthenticated(boolean isAuthenticated);

    boolean isAnonymous();
}

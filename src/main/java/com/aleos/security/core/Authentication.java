package com.aleos.security.core;

import java.util.List;

public interface Authentication {

    String getPrincipal();

    List<GrantedAuthority> getAuthorities();

    boolean isAuthenticated();

    void isAuthenticated(boolean isAuthenticated);

    boolean isAnonymous();
}

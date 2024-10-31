package com.aleos.security.core;

import java.io.Serializable;
import java.util.List;

public interface Authentication extends Serializable {

    String getPrincipal();

    List<GrantedAuthority> getAuthorities();

    boolean isAuthenticated();

    void isAuthenticated(boolean isAuthenticated);

    boolean isAnonymous();
}

package com.aleos.security.core;

import java.io.Serializable;
import java.util.List;

public interface Authentication extends Serializable {

    String getPrincipal();

    List<GrantedAuthority> getAuthorities();

    boolean setAuthenticated();

    void setAuthenticated(boolean isAuthenticated);

    boolean isAnonymous();
}

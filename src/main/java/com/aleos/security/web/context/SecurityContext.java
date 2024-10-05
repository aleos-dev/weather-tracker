package com.aleos.security.web.context;


import com.aleos.security.core.Authentication;

public interface SecurityContext {

    Authentication getAuthentication();


    void setAuthentication(Authentication authentication);
}

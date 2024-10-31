package com.aleos.security.core;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.io.Serializable;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "@class")
public interface GrantedAuthority extends Serializable {

    Role getAuthority();
}

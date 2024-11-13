package com.aleos.security.core;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.io.Serializable;

/**
 * This interface represents an authority granted to an Authentication object.
 * <p>
 * The GrantedAuthority interface should be implemented by classes that represent a specific authority
 * within a security context. An authority is usually a role or a privilege that defines the level of
 * access granted to an authenticated user.
 * <p>
 * Implementations of this interface should provide the logic to retrieve the authority.
 * <p>
 * This interface extends Serializable to allow instances of implementing classes to be serialized.
 * <p>
 * The interface uses Jackson annotations to include type information for JSON serialization and deserialization,
 * specifying that the class name should be used as the type identifier.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "@class")
public interface GrantedAuthority extends Serializable {

    Role getAuthority();
}

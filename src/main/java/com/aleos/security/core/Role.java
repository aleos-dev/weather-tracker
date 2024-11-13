package com.aleos.security.core;

/**
 * Enumeration representing different roles that can be assigned within an application.
 * <p>
 * There are three roles defined:
 * 1. ADMIN: Represents administrative privileges.
 * 2. USER: Represents regular user privileges.
 * 3. ANONYMOUS: Represents anonymous or unauthenticated user privileges.
 * <p>
 * These roles can be used to control access to various parts of an application based on
 * the assigned role of the authenticated user.
 */
public enum Role {
    ADMIN,
    USER,
    ANONYMOUS
}

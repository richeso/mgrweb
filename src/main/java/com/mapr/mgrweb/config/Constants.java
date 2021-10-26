package com.mapr.mgrweb.config;

/**
 * Application constants.
 */
public final class Constants {

    // Regex for acceptable logins
    public static final String LOGIN_REGEX = "^[_.@A-Za-z0-9-]*$";

    public static final String SYSTEM_ACCOUNT = "system";
    public static final String DEFAULT_LANGUAGE = "en";
    public static final String ANONYMOUS_USER = "anonymoususer";
    public static final String USERNAME = "userid";
    public static final String USERPASS = "password";
    public static final String ROLE_ADMIN = "ROLE_ADMIN";
    public static final String ROLE_USER = "ROLE_USER";
    public static final String ADMIN_USERNAME = "root";
    public static final String CREATED_STATUS = "CREATED";
    public static final String DELETED_STATUS = "DELETED";

    private Constants() {}
}

package com.orderfleet.webapp.config;

/**
 * Application constants.
 * 
 * @author Shaheer
 * @since May 06, 2016
 */
public final class Constants {
	//Regex for acceptable logins
    public static final String LOGIN_REGEX = "^[_'.@A-Za-z0-9-]*$";
    
    public static final String SYSTEM_ACCOUNT = "system";
    public static final String ANONYMOUS_USER = "anonymoususer";
    
    public static final String YUKTI_ERP = "YUKTI";
    public static final String MODERN_ERP = "MODERN DEALER APP";
    
    private Constants() {
    }
}

package com.orderfleet.webapp.security;

/**
 * Constants for Spring Security authorities.
 * 
 * @author Shaheer
 * @since May 06, 2016
*/
public final class AuthoritiesConstants {

	public static final String SITE_ADMIN = "ROLE_SITE_ADMIN";
	
    public static final String ADMIN = "ROLE_ADMIN";
    
    public static final String MANAGER = "ROLE_MANAGER";

    public static final String USER = "ROLE_USER";
    
    public static final String ROLE_EXECUTIVE = "ROLE_EXECUTIVE";
    
    public static final String ROLE_ADMIN_DATA = "ROLE_ADMIN_DATA";
    
    public static final String CLIENT_TP = "ROLE_CLIENT_TP";
    
    public static final String ECOM_REG = "ROLE_ECOM_REG";
    
    public static final String ECOM_GUEST = "ROLE_ECOM_GUEST";

    public static final String ANONYMOUS = "ROLE_ANONYMOUS";
    
    public static final String PARTNER = "ROLE_PARTNER";
    
    public static final String MASTER_DATA_MANAGER = "ROLE_MASTER_DATA_MANAGER";
    
    public static final String ORDERPRO_ADMIN = "ROLE_OP_ADMIN";
    
    private AuthoritiesConstants() {
    }
}

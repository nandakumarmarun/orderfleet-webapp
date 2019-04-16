package com.orderfleet.webapp.security;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

/**
 * Custom implementation of Spring UserDetails User.
 * Can be used by the application to store extra information.
 * 
 * @author Shaheer
 * @since May 14, 2016
 */
public final class UserPrincipal extends User {
	
	private static final long serialVersionUID = 1L;
	
	private Long companyId;
	
	public UserPrincipal(Long companyId, String username, String password, Collection<? extends GrantedAuthority> authorities) {
		super(username, password, authorities);
		this.companyId = companyId;
		
	}

	public Long getCompanyId() {
		return companyId;
	}

}

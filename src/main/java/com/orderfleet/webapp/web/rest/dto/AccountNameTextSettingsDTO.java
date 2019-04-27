package com.orderfleet.webapp.web.rest.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.orderfleet.webapp.domain.AccountNameTextSettings;
import com.orderfleet.webapp.domain.ProductNameTextSettings;

/**
 * A DTO for the ProductNameTextSettings entity.
 * 
 * @author Muhammed Riyas T
 * @since Dec 29, 2016
 */
public class AccountNameTextSettingsDTO {

	private String pid;

	@NotNull
	@Size(min = 1, max = 255)
	private String name;

	private boolean enabled;

	public AccountNameTextSettingsDTO() {
		super();
	}

	public AccountNameTextSettingsDTO(AccountNameTextSettings accountNameTextSettings) {
		super();
		this.pid = accountNameTextSettings.getPid();
		this.name = accountNameTextSettings.getName();
		this.enabled = accountNameTextSettings.getEnabled();
	}

	public AccountNameTextSettingsDTO( String name, boolean enabled) {
		super();
		this.name = name;
		this.enabled = enabled;
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

}
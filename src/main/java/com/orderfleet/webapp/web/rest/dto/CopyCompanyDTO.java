package com.orderfleet.webapp.web.rest.dto;

import java.util.List;

import com.orderfleet.webapp.web.rest.api.dto.UserDTO;

/**
 * A DTO for the CopyCompany entity.
 * 
 * @author Muhammed Riyas T
 * @since November 14, 2016
 */
public class CopyCompanyDTO {

	private String fromCompanyPid;
	private String legalName;
	private String email;
	private List<UserDTO> users;

	public String getFromCompanyPid() {
		return fromCompanyPid;
	}

	public void setFromCompanyPid(String fromCompanyPid) {
		this.fromCompanyPid = fromCompanyPid;
	}

	public String getLegalName() {
		return legalName;
	}

	public void setLegalName(String legalName) {
		this.legalName = legalName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public List<UserDTO> getUsers() {
		return users;
	}

	public void setUsers(List<UserDTO> users) {
		this.users = users;
	}

}

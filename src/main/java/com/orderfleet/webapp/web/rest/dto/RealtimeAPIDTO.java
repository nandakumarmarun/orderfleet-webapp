package com.orderfleet.webapp.web.rest.dto;

import com.orderfleet.webapp.domain.RealtimeAPI;

/**
 * A DTO for the RealtimeAPI entity.
 *
 * @author Sarath
 * @since Apr 10, 2018
 *
 */
public class RealtimeAPIDTO {

	private Long id;
	private String name;
	private String api;
	private String version;
	private String companyPid;
	private String companyName;
	private boolean activated = true;

	public RealtimeAPIDTO() {
		super();
	}

	public RealtimeAPIDTO(RealtimeAPI realtimeAPI) {
		super();
		this.id = realtimeAPI.getId();
		this.name = realtimeAPI.getName();
		this.api = realtimeAPI.getApi();
		this.version = realtimeAPI.getVersion();
		this.companyPid = realtimeAPI.getCompany().getPid();
		this.companyName = realtimeAPI.getCompany().getLegalName();
		this.activated = realtimeAPI.isActivated();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getApi() {
		return api;
	}

	public void setApi(String api) {
		this.api = api;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getCompanyPid() {
		return companyPid;
	}

	public void setCompanyPid(String companyPid) {
		this.companyPid = companyPid;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public boolean isActivated() {
		return activated;
	}

	public void setActivated(boolean activated) {
		this.activated = activated;
	}

	@Override
	public String toString() {
		return "RealtimeAPIDTO [name=" + name + ", api=" + api + ", version=" + version + ", companyPid=" + companyPid
				+ ", companyName=" + companyName + ", activated=" + activated + "]";
	}

}

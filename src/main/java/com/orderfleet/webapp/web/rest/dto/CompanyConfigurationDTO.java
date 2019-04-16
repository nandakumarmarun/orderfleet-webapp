package com.orderfleet.webapp.web.rest.dto;

import com.orderfleet.webapp.domain.CompanyConfiguration;
import com.orderfleet.webapp.domain.enums.CompanyConfig;

/**
 * A DTO for the CompanyConfigurationDTO entity.
 *
 * @author Sarath
 * @since Jun 23, 2017
 *
 */
public class CompanyConfigurationDTO {

	private String companyName;
	private String companyPid;
	private CompanyConfig companyConfig;
	private String value;
	private String description;

	public CompanyConfigurationDTO() {
		super();
	}

	public CompanyConfigurationDTO(CompanyConfiguration companyConfiguration) {
		super();
		this.companyName = companyConfiguration.getCompany().getLegalName();
		this.companyPid = companyConfiguration.getCompany().getPid();
		this.companyConfig = companyConfiguration.getName();
		this.value = companyConfiguration.getValue();
		this.description = companyConfiguration.getDescription();
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getCompanyPid() {
		return companyPid;
	}

	public void setCompanyPid(String companyPid) {
		this.companyPid = companyPid;
	}

	public CompanyConfig getCompanyConfig() {
		return companyConfig;
	}

	public void setCompanyConfig(CompanyConfig companyConfig) {
		this.companyConfig = companyConfig;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String toString() {
		return "CompanyConfigurationDTO [companyName=" + companyName + ", companyPid=" + companyPid + ", companyConfig="
				+ companyConfig + ", value=" + value + ", description=" + description + "]";
	}

}

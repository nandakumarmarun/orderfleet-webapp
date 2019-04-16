package com.orderfleet.webapp.web.rest.dto;

import com.orderfleet.webapp.domain.CompanyInvoiceConfiguration;

/**
 * DTO for downloading CompanyInvoiceConfiguration
 *
 * @author Sarath
 * @since Apr 13, 2018
 *
 */
public class CompanyInvoiceConfigurationDTO {

	private Long id;
	private String companyPid;
	private String companyName;
	private String address;
	private Double amountPerUser;

	public CompanyInvoiceConfigurationDTO() {
		super();
	}

	public CompanyInvoiceConfigurationDTO(CompanyInvoiceConfiguration companyInvoiceConfiguration) {
		super();
		this.id = companyInvoiceConfiguration.getId();
		this.companyPid = companyInvoiceConfiguration.getCompany().getPid();
		this.companyName = companyInvoiceConfiguration.getCompany().getLegalName();
		this.address = companyInvoiceConfiguration.getAddress();
		this.amountPerUser = companyInvoiceConfiguration.getAmountPerUser();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Double getAmountPerUser() {
		return amountPerUser;
	}

	public void setAmountPerUser(Double amountPerUser) {
		this.amountPerUser = amountPerUser;
	}

	@Override
	public String toString() {
		return "CompanyInvoiceConfigurationDTO [id=" + id + ", companyPid=" + companyPid + ", companyName="
				+ companyName + ", address=" + address + ", amountPerUser=" + amountPerUser + "]";
	}

}

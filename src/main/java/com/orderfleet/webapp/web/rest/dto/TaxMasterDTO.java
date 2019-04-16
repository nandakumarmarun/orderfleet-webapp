package com.orderfleet.webapp.web.rest.dto;

import java.io.Serializable;

import com.orderfleet.webapp.domain.TaxMaster;

/**
 * A DTO for the TaxMaster entity.
 *
 * @author Sarath
 * @since Aug 8, 2017
 *
 */
public class TaxMasterDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private String pid;
	private String vatName;
	private String description;
	private String vatClass;
	private double vatPercentage;
	private String companyName;
	private String companyPid;

	public TaxMasterDTO() {
		super();
	}

	public TaxMasterDTO(TaxMaster taxMaster) {
		super();
		this.pid = taxMaster.getPid();
		this.vatName = taxMaster.getVatName();
		this.description = taxMaster.getDescription();
		this.vatPercentage = taxMaster.getVatPercentage();
		this.companyName = taxMaster.getCompany().getLegalName();
		this.companyPid = taxMaster.getCompany().getPid();
		this.vatClass=taxMaster.getVatClass();
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public String getVatName() {
		return vatName;
	}

	public void setVatName(String vatName) {
		this.vatName = vatName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public double getVatPercentage() {
		return vatPercentage;
	}

	public void setVatPercentage(double vatPercentage) {
		this.vatPercentage = vatPercentage;
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

	public String getVatClass() {
		return vatClass;
	}

	public void setVatClass(String vatClass) {
		this.vatClass = vatClass;
	}

	@Override
	public String toString() {
		return "TaxMasterDTO [pid=" + pid + ", vatName=" + vatName + ", description=" + description + ", vatClass="
				+ vatClass + ", vatPercentage=" + vatPercentage + ", companyName=" + companyName + ", companyPid="
				+ companyPid + "]";
	}

}

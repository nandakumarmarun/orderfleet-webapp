package com.orderfleet.webapp.web.rest.integration.dto;

import java.time.LocalDate;

import com.orderfleet.webapp.domain.GSTProductGroup;

public class GSTProductGroupDTO {

	private String productGroupName;
	private LocalDate applyDate;
	private String hsnsacCode;
	private String taxType;
	private String integratedTax;
	private String centralTax;
	private String stateTax;
	private String aditionalCess;

	public GSTProductGroupDTO() {
		super();
	}

	public GSTProductGroupDTO(GSTProductGroup gstProductGroup) {
		super();
		this.productGroupName = gstProductGroup.getProductGroup().getName();
		this.applyDate = gstProductGroup.getApplyDate();
		this.hsnsacCode = gstProductGroup.getHsnsacCode();
		this.taxType = gstProductGroup.getTaxType();
		this.integratedTax = gstProductGroup.getIntegratedTax();
		this.centralTax = gstProductGroup.getCentralTax();
		this.stateTax = gstProductGroup.getStateTax();
		this.aditionalCess = gstProductGroup.getAditionalCess();
	}

	public String getProductGroupName() {
		return productGroupName;
	}

	public void setProductGroupName(String productGroupName) {
		this.productGroupName = productGroupName;
	}

	public LocalDate getApplyDate() {
		return applyDate;
	}

	public void setApplyDate(LocalDate applyDate) {
		this.applyDate = applyDate;
	}

	public String getHsnsacCode() {
		return hsnsacCode;
	}

	public void setHsnsacCode(String hsnsacCode) {
		this.hsnsacCode = hsnsacCode;
	}

	public String getTaxType() {
		return taxType;
	}

	public void setTaxType(String taxType) {
		this.taxType = taxType;
	}

	public String getIntegratedTax() {
		return integratedTax;
	}

	public void setIntegratedTax(String integratedTax) {
		this.integratedTax = integratedTax;
	}

	public String getCentralTax() {
		return centralTax;
	}

	public void setCentralTax(String centralTax) {
		this.centralTax = centralTax;
	}

	public String getStateTax() {
		return stateTax;
	}

	public void setStateTax(String stateTax) {
		this.stateTax = stateTax;
	}

	public String getAditionalCess() {
		return aditionalCess;
	}

	public void setAditionalCess(String aditionalCess) {
		this.aditionalCess = aditionalCess;
	}

	@Override
	public String toString() {
		return "GSTProductGroupWiseDTO [productGroupName=" + productGroupName + ", applyDate=" + applyDate
				+ ", hsnsacCode=" + hsnsacCode + ", taxType=" + taxType + ", integratedTax=" + integratedTax
				+ ", centralTax=" + centralTax + ", stateTax=" + stateTax + ", aditionalCess=" + aditionalCess + "]";
	}

}

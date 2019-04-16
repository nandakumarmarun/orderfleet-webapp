package com.orderfleet.webapp.web.rest.dto;

/**
 *
 * @author Sarath
 * @since Nov 9, 2016
 */
public class VatLedgerDTO {

	private String name;
	private String percentageOfCalculation;
	private String vatClass;
	
	public VatLedgerDTO() {
		super();
	}

//	public VatLedgerDTO(AccountProfileDTO accountProfileDTO) {
//		super();
//		this.name = accountProfileDTO.getName();
//		this.percentageOfCalculation = accountProfileDTO.getAlias();
//	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPercentageOfCalculation() {
		return percentageOfCalculation;
	}

	public void setPercentageOfCalculation(String percentageOfCalculation) {
		this.percentageOfCalculation = percentageOfCalculation;
	}

	public String getVatClass() {
		return vatClass;
	}

	public void setVatClass(String vatClass) {
		this.vatClass = vatClass;
	}

	@Override
	public String toString() {
		return "VatLedgerDTO [name=" + name + ", percentageOfCalculation=" + percentageOfCalculation + "]";
	}

}

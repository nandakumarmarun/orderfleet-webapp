package com.orderfleet.webapp.web.rest.dto;

/**
 * A DTO for the CompanyConfigurationDTO entity.
 *
 * @author Sarath
 * @since Jul 28, 2017
 *
 */
public class CompanyConfigDTO {

	private String companyName;
	private String companyPid;
	private boolean distanceTraveled;
	private boolean locationVariance;
	private boolean interimSave;
	private boolean refreshProductGroupProduct;
	private boolean stageChangeAccountingVoucher;

	public CompanyConfigDTO() {
		super();
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

	public boolean isDistanceTraveled() {
		return distanceTraveled;
	}

	public void setDistanceTraveled(boolean distanceTraveled) {
		this.distanceTraveled = distanceTraveled;
	}

	public boolean isLocationVariance() {
		return locationVariance;
	}

	public void setLocationVariance(boolean locationVariance) {
		this.locationVariance = locationVariance;
	}

	public boolean getInterimSave() {
		return interimSave;
	}

	public void setInterimSave(boolean interimSave) {
		this.interimSave = interimSave;
	}

	public boolean isRefreshProductGroupProduct() {
		return refreshProductGroupProduct;
	}

	public void setRefreshProductGroupProduct(boolean refreshProductGroupProduct) {
		this.refreshProductGroupProduct = refreshProductGroupProduct;
	}

	public boolean isStageChangeAccountingVoucher() {
		return stageChangeAccountingVoucher;
	}

	public void setStageChangeAccountingVoucher(boolean stageChangeAccountingVoucher) {
		this.stageChangeAccountingVoucher = stageChangeAccountingVoucher;
	}

}

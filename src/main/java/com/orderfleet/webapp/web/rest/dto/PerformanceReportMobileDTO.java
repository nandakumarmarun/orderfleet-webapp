package com.orderfleet.webapp.web.rest.dto;

import com.orderfleet.webapp.domain.PerformanceReportMobile;
import com.orderfleet.webapp.domain.enums.MobileUINames;

public class PerformanceReportMobileDTO {

	private MobileUINames mobileUINames;

	private SalesTargetReportSettingDTO salesTargetReportSetting;

	public PerformanceReportMobileDTO() {
		super();
	}

	public PerformanceReportMobileDTO(PerformanceReportMobile mobileMenuItemGroup) {
		super();
		this.mobileUINames = mobileMenuItemGroup.getMobileUINames();
		this.salesTargetReportSetting = new SalesTargetReportSettingDTO(
				mobileMenuItemGroup.getSalesTargetReportSetting());
	}

	public MobileUINames getMobileUINames() {
		return mobileUINames;
	}

	public void setMobileUINames(MobileUINames mobileUINames) {
		this.mobileUINames = mobileUINames;
	}

	public SalesTargetReportSettingDTO getSalesTargetReportSetting() {
		return salesTargetReportSetting;
	}

	public void setSalesTargetReportSetting(SalesTargetReportSettingDTO salesTargetReportSetting) {
		this.salesTargetReportSetting = salesTargetReportSetting;
	}

}

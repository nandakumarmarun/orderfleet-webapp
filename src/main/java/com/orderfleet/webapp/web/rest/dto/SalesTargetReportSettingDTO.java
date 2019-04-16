package com.orderfleet.webapp.web.rest.dto;

import java.util.List;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.orderfleet.webapp.domain.SalesTargetReportSetting;
import com.orderfleet.webapp.domain.enums.BestPerformanceType;
import com.orderfleet.webapp.domain.enums.MobileUINames;
import com.orderfleet.webapp.domain.enums.TargetFrequency;
import com.orderfleet.webapp.domain.enums.TargetType;

/**
 * DTO object for SalesTargetReportSetting
 *
 * @author Sarath
 * @since Feb 17, 2017
 */
public class SalesTargetReportSettingDTO {

	private String pid;

	@NotNull
	@Size(min = 1, max = 255)
	private String name;

	private boolean accountWiseTarget;

	@NotNull
	private TargetFrequency targetPeriod = TargetFrequency.MONTH;

	@NotNull
	private TargetType targetType = TargetType.NONE;

	private List<SalesTargetBlockDTO> salesTargetBlocks;

	private MobileUINames mobileUIName;

	private boolean monthlyAverageWise;

	private BestPerformanceType targetSettingType;

	public SalesTargetReportSettingDTO() {
		super();
	}

	public SalesTargetReportSettingDTO(SalesTargetReportSetting salesTargetReportSetting) {
		super();
		this.pid = salesTargetReportSetting.getPid();
		this.name = salesTargetReportSetting.getName();
		this.accountWiseTarget = salesTargetReportSetting.getAccountWiseTarget();
		this.targetPeriod = salesTargetReportSetting.getTargetPeriod();
		this.targetType = salesTargetReportSetting.getTargetType();
		this.monthlyAverageWise = salesTargetReportSetting.getMonthlyAverageWise();
		this.targetSettingType = salesTargetReportSetting.getTargetSettingType();
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

	public boolean getAccountWiseTarget() {
		return accountWiseTarget;
	}

	public void setAccountWiseTarget(boolean accountWiseTarget) {
		this.accountWiseTarget = accountWiseTarget;
	}

	public TargetFrequency getTargetPeriod() {
		return targetPeriod;
	}

	public void setTargetPeriod(TargetFrequency targetPeriod) {
		this.targetPeriod = targetPeriod;
	}

	public TargetType getTargetType() {
		return targetType;
	}

	public void setTargetType(TargetType targetType) {
		this.targetType = targetType;
	}

	public List<SalesTargetBlockDTO> getSalesTargetBlocks() {
		return salesTargetBlocks;
	}

	public void setSalesTargetBlocks(List<SalesTargetBlockDTO> salesTargetBlocks) {
		this.salesTargetBlocks = salesTargetBlocks;
	}

	public MobileUINames getMobileUIName() {
		return mobileUIName;
	}

	public void setMobileUIName(MobileUINames mobileUIName) {
		this.mobileUIName = mobileUIName;
	}

	public boolean getMonthlyAverageWise() {
		return monthlyAverageWise;
	}

	public void setMonthlyAverageWise(boolean monthlyAverageWise) {
		this.monthlyAverageWise = monthlyAverageWise;
	}

	public BestPerformanceType getTargetSettingType() {
		return targetSettingType;
	}

	public void setTargetSettingType(BestPerformanceType targetSettingType) {
		this.targetSettingType = targetSettingType;
	}

	@Override
	public String toString() {
		return "SalesTargetReportSettingDTO [pid=" + pid + ", name=" + name + ", accountWiseTarget=" + accountWiseTarget
				+ ", targetPeriod=" + targetPeriod + ", targetType=" + targetType + "]";
	}

}

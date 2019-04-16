package com.orderfleet.webapp.web.rest.dto;

import com.orderfleet.webapp.domain.SalesTargetReportSettingSalesTargetBlock;

public class SalesTargetReportSettingSalesTargetBlockDTO {

	private String salesTargetReportSettingPid;

	private String salesTargetReportSettingName;

	private String salesTargetBlockPid;

	private String salesTargetBlockName;

	private int sortOrder = 0;

	public SalesTargetReportSettingSalesTargetBlockDTO() {
	}

	public SalesTargetReportSettingSalesTargetBlockDTO(
			SalesTargetReportSettingSalesTargetBlock salesTargetReportSettingSalesTargetBlock) {
		super();
		this.salesTargetReportSettingPid = salesTargetReportSettingSalesTargetBlock.getSalesTargetReportSetting()
				.getPid();
		this.salesTargetReportSettingName = salesTargetReportSettingSalesTargetBlock.getSalesTargetReportSetting()
				.getName();
		this.salesTargetBlockPid = salesTargetReportSettingSalesTargetBlock.getSalesTargetBlock().getPid();
		this.salesTargetBlockName = salesTargetReportSettingSalesTargetBlock.getSalesTargetBlock().getName();
		this.sortOrder = salesTargetReportSettingSalesTargetBlock.getSortOrder();
	}

	public String getSalesTargetReportSettingPid() {
		return salesTargetReportSettingPid;
	}

	public void setSalesTargetReportSettingPid(String salesTargetReportSettingPid) {
		this.salesTargetReportSettingPid = salesTargetReportSettingPid;
	}

	public String getSalesTargetReportSettingName() {
		return salesTargetReportSettingName;
	}

	public void setSalesTargetReportSettingName(String salesTargetReportSettingName) {
		this.salesTargetReportSettingName = salesTargetReportSettingName;
	}

	public String getSalesTargetBlockPid() {
		return salesTargetBlockPid;
	}

	public void setSalesTargetBlockPid(String salesTargetBlockPid) {
		this.salesTargetBlockPid = salesTargetBlockPid;
	}

	public String getSalesTargetBlockName() {
		return salesTargetBlockName;
	}

	public void setSalesTargetBlockName(String salesTargetBlockName) {
		this.salesTargetBlockName = salesTargetBlockName;
	}

	public int getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(int sortOrder) {
		this.sortOrder = sortOrder;
	}

}

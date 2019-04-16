package com.orderfleet.webapp.web.rest.dto;

/**
 * A DTO for the SalesTargetBlockSalesTargetGroup entity.
 *
 * @author Sarath
 * @since Feb 22, 2017
 */
public class SalesTargetBlockSalesTargetGroupDTO {

	private String salesTargetBlockPid;
	private String salesTargetBlockName;
	private String salesTargetGroupPid;
	private String salesTargetGroupName;

	public SalesTargetBlockSalesTargetGroupDTO(String salesTargetBlockPid, String salesTargetBlockName,
			String salesTargetGroupPid, String salesTargetGroupName) {
		super();
		this.salesTargetBlockPid = salesTargetBlockPid;
		this.salesTargetBlockName = salesTargetBlockName;
		this.salesTargetGroupPid = salesTargetGroupPid;
		this.salesTargetGroupName = salesTargetGroupName;
	}

	public SalesTargetBlockSalesTargetGroupDTO() {
		super();
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

	public String getSalesTargetGroupPid() {
		return salesTargetGroupPid;
	}

	public void setSalesTargetGroupPid(String salesTargetGroupPid) {
		this.salesTargetGroupPid = salesTargetGroupPid;
	}

	public String getSalesTargetGroupName() {
		return salesTargetGroupName;
	}

	public void setSalesTargetGroupName(String salesTargetGroupName) {
		this.salesTargetGroupName = salesTargetGroupName;
	}

}

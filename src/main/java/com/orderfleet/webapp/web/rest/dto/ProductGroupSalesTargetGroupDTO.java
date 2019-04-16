package com.orderfleet.webapp.web.rest.dto;

import java.util.List;

public class ProductGroupSalesTargetGroupDTO {

	private Long id;

	private String productGroupName;

	List<SalesTargetGroupDTO> salesTargetGroups;

	public ProductGroupSalesTargetGroupDTO() {
		super();
		// TODO Auto-generated constructor stub
	}

	public ProductGroupSalesTargetGroupDTO(String productGroupName, List<SalesTargetGroupDTO> salesTargetGroups) {
		super();
		this.productGroupName = productGroupName;
		this.salesTargetGroups = salesTargetGroups;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getProductGroupName() {
		return productGroupName;
	}

	public void setProductGroupName(String productGroupName) {
		this.productGroupName = productGroupName;
	}

	public List<SalesTargetGroupDTO> getSalesTargetGroups() {
		return salesTargetGroups;
	}

	public void setSalesTargetGroups(List<SalesTargetGroupDTO> salesTargetGroups) {
		this.salesTargetGroups = salesTargetGroups;
	}

	@Override
	public String toString() {
		return "ProductGroupSalesTargetGroupDTO [productGroupName=" + productGroupName + ", salesTargetGroups="
				+ salesTargetGroups + "]";
	}

}

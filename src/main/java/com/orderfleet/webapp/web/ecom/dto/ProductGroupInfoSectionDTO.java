package com.orderfleet.webapp.web.ecom.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.orderfleet.webapp.domain.ProductGroupInfoSection;

/**
 * A DTO for the ProductGroupInfoSection entity.
 * 
 * @author Muhammed Riyas T
 * @since Sep 21, 2016
 */
public class ProductGroupInfoSectionDTO {

	private String pid;

	@NotNull
	@Size(min = 1, max = 255)
	private String name;

	private String richText;

	@NotNull
	private String productGroupPid;

	private String productGroupName;

	public ProductGroupInfoSectionDTO() {
		super();
	}
	
	public ProductGroupInfoSectionDTO(String pid, String name, String richText, String productGroupPid,
			String productGroupName) {
		super();
		this.pid = pid;
		this.name = name;
		this.richText = richText;
		this.productGroupPid = productGroupPid;
		this.productGroupName = productGroupName;
	}

	public ProductGroupInfoSectionDTO(ProductGroupInfoSection productGroupInfoSection) {
		super();
		this.pid = productGroupInfoSection.getPid();
		this.name = productGroupInfoSection.getName();
		this.productGroupPid = productGroupInfoSection.getProductGroup().getPid();
		this.productGroupName = productGroupInfoSection.getProductGroup().getName();
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

	public String getRichText() {
		return richText;
	}

	public void setRichText(String richText) {
		this.richText = richText;
	}

	public String getProductGroupPid() {
		return productGroupPid;
	}

	public void setProductGroupPid(String productGroupPid) {
		this.productGroupPid = productGroupPid;
	}

	public String getProductGroupName() {
		return productGroupName;
	}

	public void setProductGroupName(String productGroupName) {
		this.productGroupName = productGroupName;
	}

}

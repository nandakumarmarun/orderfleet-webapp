package com.orderfleet.webapp.web.rest.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.orderfleet.webapp.domain.EcomProductProfile;

/**
 * A DTO for the EcomProductGroup entity.
 * 
 * @author Sarath
 * @since Sep 23, 2016
 */
public class EcomProductProfileDTO {

	private String pid;
	private String name;
	private String alias;
	private String description;
	private String ecomDisplayAttributes;
	private String productGroupPid;
	private String productGroupName;
	private String offerString;
	private List<ProductProfileDTO> productProfiles = new ArrayList<>();
	private boolean activated;
	private LocalDateTime lastModifiedDate;
	private byte[] image;
	private String imageContentType;

	public EcomProductProfileDTO() {
		super();
	}

	public EcomProductProfileDTO(EcomProductProfile ecomProductProfile) {
		super();
		this.pid = ecomProductProfile.getPid();
		this.name = ecomProductProfile.getName();
		this.alias = ecomProductProfile.getAlias();
		this.description = ecomProductProfile.getDescription();
		this.ecomDisplayAttributes = ecomProductProfile.getEcomDisplayAttributes();
		this.offerString = ecomProductProfile.getOfferString();
		this.lastModifiedDate = ecomProductProfile.getLastModifiedDate();
		this.image = ecomProductProfile.getImage();
		this.imageContentType = ecomProductProfile.getImageContentType();
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

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getEcomDisplayAttributes() {
		return ecomDisplayAttributes;
	}

	public void setEcomDisplayAttributes(String ecomDisplayAttributes) {
		this.ecomDisplayAttributes = ecomDisplayAttributes;
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

	public String getOfferString() {
		return offerString;
	}

	public void setOfferString(String offerString) {
		this.offerString = offerString;
	}

	public List<ProductProfileDTO> getProductProfiles() {
		return productProfiles;
	}

	public void setProductProfiles(List<ProductProfileDTO> productProfiles) {
		this.productProfiles = productProfiles;
	}

	public boolean getActivated() {
		return activated;
	}

	public void setActivated(boolean activated) {
		this.activated = activated;
	}

	public LocalDateTime getLastModifiedDate() {
		return lastModifiedDate;
	}

	public void setLastModifiedDate(LocalDateTime lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}

	public byte[] getImage() {
		return image;
	}

	public void setImage(byte[] image) {
		this.image = image;
	}

	public String getImageContentType() {
		return imageContentType;
	}

	public void setImageContentType(String imageContentType) {
		this.imageContentType = imageContentType;
	}

	@Override
	public String toString() {
		return "EcomProductGroupDTO [pid=" + pid + ", name=" + name + ", alias=" + alias + ", description="
				+ description + ", ecomDisplayAttributes=" + ecomDisplayAttributes + "]";
	}

}

package com.orderfleet.webapp.web.vendor.integre.dto;

import java.time.LocalDateTime;
import java.util.Objects;

import com.orderfleet.webapp.domain.PriceLevelList;

public class PriceLevelListVendorDTO {

	private String pid;

	private String priceLevelPid;

	private String priceLevelName;
	
	private String priceLevelCode;

	private String productProfilePid;

	private String productProfileName;
	
	private String productProfileCode;

	private double price;

	private double rangeFrom;

	private double rangeTo;
	
	private LocalDateTime lastModifiedDate;

	
	public PriceLevelListVendorDTO() {
	}

	public PriceLevelListVendorDTO(PriceLevelList priceLevelList) {
		super();
		this.pid = priceLevelList.getPid();
		this.priceLevelPid = priceLevelList.getPriceLevel().getPid();
		this.priceLevelName = priceLevelList.getPriceLevel().getName();
		this.priceLevelCode = priceLevelList.getPriceLevel().getAlias();
		this.productProfilePid = priceLevelList.getProductProfile().getPid();
		this.productProfileName = priceLevelList.getProductProfile().getName();
		this.productProfileCode = priceLevelList.getProductProfile().getAlias();
		this.price = priceLevelList.getPrice();
		this.rangeFrom = priceLevelList.getRangeFrom();
		this.rangeTo = priceLevelList.getRangeTo();
		this.lastModifiedDate=priceLevelList.getLastModifiedDate();
	}
	

	
	
	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public String getPriceLevelPid() {
		return priceLevelPid;
	}

	public void setPriceLevelPid(String priceLevelPid) {
		this.priceLevelPid = priceLevelPid;
	}

	public String getPriceLevelName() {
		return priceLevelName;
	}

	public void setPriceLevelName(String priceLevelName) {
		this.priceLevelName = priceLevelName;
	}

	public String getPriceLevelCode() {
		return priceLevelCode;
	}

	public void setPriceLevelCode(String priceLevelCode) {
		this.priceLevelCode = priceLevelCode;
	}

	public String getProductProfilePid() {
		return productProfilePid;
	}

	public void setProductProfilePid(String productProfilePid) {
		this.productProfilePid = productProfilePid;
	}

	public String getProductProfileName() {
		return productProfileName;
	}

	public void setProductProfileName(String productProfileName) {
		this.productProfileName = productProfileName;
	}

	public String getProductProfileCode() {
		return productProfileCode;
	}

	public void setProductProfileCode(String productProfileCode) {
		this.productProfileCode = productProfileCode;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public double getRangeFrom() {
		return rangeFrom;
	}

	public void setRangeFrom(double rangeFrom) {
		this.rangeFrom = rangeFrom;
	}

	public double getRangeTo() {
		return rangeTo;
	}

	public void setRangeTo(double rangeTo) {
		this.rangeTo = rangeTo;
	}

	public LocalDateTime getLastModifiedDate() {
		return lastModifiedDate;
	}

	public void setLastModifiedDate(LocalDateTime lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		PriceLevelListVendorDTO priceLevelVendorListDTO = (PriceLevelListVendorDTO) o;

		if (!Objects.equals(pid, priceLevelVendorListDTO.pid))
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(pid);
	}
	
}

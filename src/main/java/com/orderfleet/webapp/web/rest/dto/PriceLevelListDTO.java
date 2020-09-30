package com.orderfleet.webapp.web.rest.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

import com.orderfleet.webapp.domain.PriceLevelList;

/**
 * A DTO for the PriceLevelList entity.
 * 
 * @author Muhammed Riyas T
 * @since August 22, 2016
 */
public class PriceLevelListDTO {

	private String pid;

	private String priceLevelPid;

	private String priceLevelName;

	private String productProfilePid;

	private String productProfileName;

	private double price;

	private double rangeFrom;

	private double rangeTo;

	private LocalDateTime lastModifiedDate;

	public PriceLevelListDTO(PriceLevelList priceLevelList) {
		super();
		this.pid = priceLevelList.getPid();
		this.priceLevelPid = priceLevelList.getPriceLevel().getPid();
		this.priceLevelName = priceLevelList.getPriceLevel().getName();
		this.productProfilePid = priceLevelList.getProductProfile().getPid();
		this.productProfileName = priceLevelList.getProductProfile().getName();
		this.price = priceLevelList.getPrice();
		this.rangeFrom = priceLevelList.getRangeFrom();
		this.rangeTo = priceLevelList.getRangeTo();
		this.lastModifiedDate = priceLevelList.getLastModifiedDate();
	}

	public PriceLevelListDTO(String productProfileName, String priceLevelName, double price, double rangeFrom,
			double rangeTo) {
		super();
		this.productProfileName = productProfileName;
		this.priceLevelName = priceLevelName;
		this.price = price;
		this.rangeFrom = rangeFrom;
		this.rangeTo = rangeTo;
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

		PriceLevelListDTO priceLevelListDTO = (PriceLevelListDTO) o;

		if (!Objects.equals(pid, priceLevelListDTO.pid))
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(pid);
	}

	@Override
	public String toString() {
		return "PriceLevelListDTO{" + ", pid='" + pid + "'" + ", priceLevelName='" + priceLevelName + "'"
				+ ", productProfileName='" + productProfileName + "'" + ", price='" + price + "'" + '}';
	}
}

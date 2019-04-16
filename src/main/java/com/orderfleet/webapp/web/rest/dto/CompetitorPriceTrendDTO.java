package com.orderfleet.webapp.web.rest.dto;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * A DTO for the CompetitorProfile entity.
 * 
 * @author Muhammed Riyas T
 * @since August 26, 2016
 */
public class CompetitorPriceTrendDTO {

	private String pid;

	private String priceTrendProductGroupPid;

	private String priceTrendProductGroupName;

	private String priceTrendProductPid;

	private String priceTrendProductName;

	private String competitorProfilePid;

	private String competitorProfileName;

	private String userName;

	private double price1;

	private double price2;

	private double price3;

	private double price4;

	private double price5;

	private String remarks;

	private LocalDateTime createdDate;

	public CompetitorPriceTrendDTO() {
		super();
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public String getPriceTrendProductGroupPid() {
		return priceTrendProductGroupPid;
	}

	public void setPriceTrendProductGroupPid(String priceTrendProductGroupPid) {
		this.priceTrendProductGroupPid = priceTrendProductGroupPid;
	}

	public String getPriceTrendProductGroupName() {
		return priceTrendProductGroupName;
	}

	public void setPriceTrendProductGroupName(String priceTrendProductGroupName) {
		this.priceTrendProductGroupName = priceTrendProductGroupName;
	}

	public String getPriceTrendProductPid() {
		return priceTrendProductPid;
	}

	public void setPriceTrendProductPid(String priceTrendProductPid) {
		this.priceTrendProductPid = priceTrendProductPid;
	}

	public String getPriceTrendProductName() {
		return priceTrendProductName;
	}

	public void setPriceTrendProductName(String priceTrendProductName) {
		this.priceTrendProductName = priceTrendProductName;
	}

	public String getCompetitorProfilePid() {
		return competitorProfilePid;
	}

	public void setCompetitorProfilePid(String competitorProfilePid) {
		this.competitorProfilePid = competitorProfilePid;
	}

	public String getCompetitorProfileName() {
		return competitorProfileName;
	}

	public void setCompetitorProfileName(String competitorProfileName) {
		this.competitorProfileName = competitorProfileName;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public double getPrice1() {
		return price1;
	}

	public void setPrice1(double price1) {
		this.price1 = price1;
	}

	public double getPrice2() {
		return price2;
	}

	public void setPrice2(double price2) {
		this.price2 = price2;
	}

	public double getPrice3() {
		return price3;
	}

	public void setPrice3(double price3) {
		this.price3 = price3;
	}

	public double getPrice4() {
		return price4;
	}

	public void setPrice4(double price4) {
		this.price4 = price4;
	}

	public double getPrice5() {
		return price5;
	}

	public void setPrice5(double price5) {
		this.price5 = price5;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public LocalDateTime getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(LocalDateTime createdDate) {
		this.createdDate = createdDate;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		CompetitorPriceTrendDTO competitorPriceTrendDTO = (CompetitorPriceTrendDTO) o;

		if (!Objects.equals(pid, competitorPriceTrendDTO.pid))
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(pid);
	}

}

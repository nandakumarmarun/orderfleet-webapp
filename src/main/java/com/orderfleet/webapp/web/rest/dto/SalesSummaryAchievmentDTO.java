package com.orderfleet.webapp.web.rest.dto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.databind.introspect.POJOPropertiesCollector;
import com.orderfleet.webapp.domain.SalesSummaryAchievment;

/**
 * A DTO for the AccountGroup entity.
 * 
 * @author Muhammed Riyas T
 * @since October 17, 2016
 */
public class SalesSummaryAchievmentDTO {

	private String pid;

	private String salesTargetGroupPid;

	private String salesTargetGroupName;

	private String userPid;

	private String userName;

	private double amount;

	private LocalDate achievedDate;
	
	private String locationPid;
	
	private String locationName;
	
	private String employeePid;

	private String employeeName;

	public SalesSummaryAchievmentDTO() {
	}

	public SalesSummaryAchievmentDTO(SalesSummaryAchievment salesSummaryAchievment) {
		super();
		this.pid = salesSummaryAchievment.getPid();
		this.salesTargetGroupPid = salesSummaryAchievment.getSalesTargetGroup().getPid();
		this.salesTargetGroupName = salesSummaryAchievment.getSalesTargetGroup().getName();
		this.userPid = salesSummaryAchievment.getUser().getPid();
		this.userName = salesSummaryAchievment.getUser().getFirstName();
		this.amount = salesSummaryAchievment.getAmount();
		this.achievedDate = salesSummaryAchievment.getAchievedDate();
		this.locationPid=salesSummaryAchievment.getLocation().getPid();
		this.locationName=salesSummaryAchievment.getLocation().getName();
	}
List<SalesSummaryAchievmentDTO> DTOList = new ArrayList<>();
	public List<SalesSummaryAchievmentDTO> convertToDTo(List<SalesSummaryAchievment> salesSummaryAchievments)
	{

		for (SalesSummaryAchievment salesSummaryAchievment : salesSummaryAchievments) {
			SalesSummaryAchievmentDTO achievment = new SalesSummaryAchievmentDTO();
			achievment.setPid(salesSummaryAchievment.getPid());
			achievment.setSalesTargetGroupPid(salesSummaryAchievment.getSalesTargetGroup().getPid());
			achievment.setSalesTargetGroupName(salesSummaryAchievment.getSalesTargetGroup().getName());
			achievment.setAmount(salesSummaryAchievment.getAmount());
			achievment.setAchievedDate(salesSummaryAchievment.getAchievedDate());

			DTOList.add(achievment);
		}
		return DTOList;
	}
	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
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

	public String getUserPid() {
		return userPid;
	}

	public void setUserPid(String userPid) {
		this.userPid = userPid;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public LocalDate getAchievedDate() {
		return achievedDate;
	}

	public void setAchievedDate(LocalDate achievedDate) {
		this.achievedDate = achievedDate;
	}

	public String getLocationPid() {
		return locationPid;
	}

	public void setLocationPid(String locationPid) {
		this.locationPid = locationPid;
	}

	public String getLocationName() {
		return locationName;
	}

	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}

	public String getEmployeePid() {
		return employeePid;
	}

	public void setEmployeePid(String employeePid) {
		this.employeePid = employeePid;
	}

	public String getEmployeeName() {
		return employeeName;
	}

	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		SalesSummaryAchievmentDTO salesSummaryAchievmentDTO = (SalesSummaryAchievmentDTO) o;

		if (!Objects.equals(pid, salesSummaryAchievmentDTO.pid))
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(pid);
	}

	@Override
	public String toString() {
		return "SalesSummaryAchievmentDTO [pid=" + pid + ", salesTargetGroupName=" + salesTargetGroupName
				+ ", userName=" + userName + ", amount=" + amount + ", achievedDate=" + achievedDate + ", locationName="
				+ locationName + ", employeeName=" + employeeName + "]";
	}



}
package com.orderfleet.webapp.web.rest.dto;

import java.util.Objects;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.orderfleet.webapp.domain.SalesTargetBlock;
import com.orderfleet.webapp.domain.enums.BestPerformanceType;

/**
 * A DTO for the SalesTargetBlock entity.
 * 
 * @author Sarath
 * @since Feb 17, 2017
 */
public class SalesTargetBlockDTO {

	private String pid;

	@NotNull
	@Size(min = 1, max = 255)
	private String name;

	private int startMonth;

	private String startMonthName;

	private int startMonthMinus;

	private int startMonthYearMinus;

	private int endMonth;

	private String endMonthName;

	private int endMonthMinus;

	private int endMonthYearMinus;

	private boolean createDynamicLabel;

	private String description;

	private int sortOrder;

	private double targetAmount;

	private double achievedAmount;

	private double targetVolume;

	private double achievedVolume;

	private BestPerformanceType targetSettingType;

	public SalesTargetBlockDTO() {
		super();
	}

	public SalesTargetBlockDTO(SalesTargetBlock salesTargetBlock) {
		super();
		this.pid = salesTargetBlock.getPid();
		this.name = salesTargetBlock.getName();
		this.startMonth = salesTargetBlock.getStartMonth();
		this.startMonthName = salesTargetBlock.getStartMonthName();
		this.startMonthMinus = salesTargetBlock.getStartMonthMinus();
		this.startMonthYearMinus = salesTargetBlock.getStartMonthYearMinus();
		this.endMonth = salesTargetBlock.getEndMonth();
		this.endMonthName = salesTargetBlock.getEndMonthName();
		this.endMonthMinus = salesTargetBlock.getEndMonthMinus();
		this.endMonthYearMinus = salesTargetBlock.getEndMonthYearMinus();
		this.createDynamicLabel = salesTargetBlock.getCreateDynamicLabel();
		this.description = salesTargetBlock.getDescription();
		this.targetSettingType = salesTargetBlock.getTargetSettingType();
	}

	public SalesTargetBlockDTO(SalesTargetBlock salesTargetBlock, int sortOrder) {
		super();
		this.pid = salesTargetBlock.getPid();
		this.name = salesTargetBlock.getName();
		this.startMonth = salesTargetBlock.getStartMonth();
		this.startMonthName = salesTargetBlock.getStartMonthName();
		this.startMonthMinus = salesTargetBlock.getStartMonthMinus();
		this.startMonthYearMinus = salesTargetBlock.getStartMonthYearMinus();
		this.endMonth = salesTargetBlock.getEndMonth();
		this.endMonthName = salesTargetBlock.getEndMonthName();
		this.endMonthMinus = salesTargetBlock.getEndMonthMinus();
		this.endMonthYearMinus = salesTargetBlock.getEndMonthYearMinus();
		this.createDynamicLabel = salesTargetBlock.getCreateDynamicLabel();
		this.description = salesTargetBlock.getDescription();
		this.sortOrder = sortOrder;
		this.targetSettingType = salesTargetBlock.getTargetSettingType();
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

	public int getStartMonth() {
		return startMonth;
	}

	public void setStartMonth(int startMonth) {
		this.startMonth = startMonth;
	}

	public int getStartMonthMinus() {
		return startMonthMinus;
	}

	public void setStartMonthMinus(int startMonthMinus) {
		this.startMonthMinus = startMonthMinus;
	}

	public int getStartMonthYearMinus() {
		return startMonthYearMinus;
	}

	public void setStartMonthYearMinus(int startMonthYearMinus) {
		this.startMonthYearMinus = startMonthYearMinus;
	}

	public int getEndMonth() {
		return endMonth;
	}

	public void setEndMonth(int endMonth) {
		this.endMonth = endMonth;
	}

	public int getEndMonthMinus() {
		return endMonthMinus;
	}

	public void setEndMonthMinus(int endMonthMinus) {
		this.endMonthMinus = endMonthMinus;
	}

	public int getEndMonthYearMinus() {
		return endMonthYearMinus;
	}

	public void setEndMonthYearMinus(int endMonthYearMinus) {
		this.endMonthYearMinus = endMonthYearMinus;
	}

	public boolean getCreateDynamicLabel() {
		return createDynamicLabel;
	}

	public void setCreateDynamicLabel(boolean createDynamicLabel) {
		this.createDynamicLabel = createDynamicLabel;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getStartMonthName() {
		return startMonthName;
	}

	public void setStartMonthName(String startMonthName) {
		this.startMonthName = startMonthName;
	}

	public String getEndMonthName() {
		return endMonthName;
	}

	public void setEndMonthName(String endMonthName) {
		this.endMonthName = endMonthName;
	}

	public int getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(int sortOrder) {
		this.sortOrder = sortOrder;
	}

	public double getTargetAmount() {
		return targetAmount;
	}

	public void setTargetAmount(double targetAmount) {
		this.targetAmount = targetAmount;
	}

	public double getAchievedAmount() {
		return achievedAmount;
	}

	public void setAchievedAmount(double achievedAmount) {
		this.achievedAmount = achievedAmount;
	}

	public double getTargetVolume() {
		return targetVolume;
	}

	public void setTargetVolume(double targetVolume) {
		this.targetVolume = targetVolume;
	}

	public double getAchievedVolume() {
		return achievedVolume;
	}

	public void setAchievedVolume(double achievedVolume) {
		this.achievedVolume = achievedVolume;
	}

	public BestPerformanceType getTargetSettingType() {
		return targetSettingType;
	}

	public void setTargetSettingType(BestPerformanceType targetSettingType) {
		this.targetSettingType = targetSettingType;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		SalesTargetBlockDTO salesTargetBlockDTO = (SalesTargetBlockDTO) o;

		if (!Objects.equals(pid, salesTargetBlockDTO.pid))
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(pid);
	}

	@Override
	public String toString() {
		return "SalesTargetBlockDTO{" + ", pid='" + pid + "'" + ", name='" + name + "'" + ", description='"
				+ description + "'" + '}';
	}
}

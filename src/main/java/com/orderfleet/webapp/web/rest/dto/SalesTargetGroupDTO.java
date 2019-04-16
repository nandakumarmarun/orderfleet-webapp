package com.orderfleet.webapp.web.rest.dto;

import java.util.Objects;

import com.orderfleet.webapp.domain.SalesTargetGroup;
import com.orderfleet.webapp.domain.enums.BestPerformanceType;

/**
 * A SalesTargetGroupDTO
 * 
 * @author Sarath
 * @since Aug 12, 2016
 */
public class SalesTargetGroupDTO {

	private String pid;
	private String name;
	private String alias;
	private String description;
	private String targetUnit;
	private BestPerformanceType targetSettingType;

	public SalesTargetGroupDTO() {
		super();
	}

	public SalesTargetGroupDTO(SalesTargetGroup salesTargetGroup) {
		super();
		this.pid = salesTargetGroup.getPid();
		this.name = salesTargetGroup.getName();
		this.alias = salesTargetGroup.getAlias();
		this.description = salesTargetGroup.getDescription();
		this.targetUnit = salesTargetGroup.getTargetUnit();
		this.targetSettingType = salesTargetGroup.getTargetSettingType();
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

	public String getTargetUnit() {
		return targetUnit;
	}

	public void setTargetUnit(String targetUnit) {
		this.targetUnit = targetUnit;
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

		SalesTargetGroupDTO bankDTO = (SalesTargetGroupDTO) o;

		if (!Objects.equals(pid, bankDTO.pid))
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(pid);
	}

	@Override
	public String toString() {
		return "SalesTargetGroupDTO [pid=" + pid + ", name=" + name + ", alias=" + alias + ", description="
				+ description + ", targetUnit=" + targetUnit + "]";
	}

}

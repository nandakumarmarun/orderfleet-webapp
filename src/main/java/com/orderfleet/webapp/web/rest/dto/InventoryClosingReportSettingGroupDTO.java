package com.orderfleet.webapp.web.rest.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.orderfleet.webapp.domain.InventoryClosingReportSettingGroup;
import com.orderfleet.webapp.domain.enums.Flow;

public class InventoryClosingReportSettingGroupDTO implements Comparable<InventoryClosingReportSettingGroupDTO> {
	
	private String pid;

	@NotNull
	@Size(min = 1, max = 255)
	private String name;

	@Size(max = 55)
	private String alias;

	private String description;

	private boolean activated;
	
	private Flow flow;
	
	private Long sortOrder;

	public InventoryClosingReportSettingGroupDTO() {}

	public InventoryClosingReportSettingGroupDTO(String pid, String name, String alias, String description,Flow flow, Long sortOrder,
			boolean activated) {
		super();
		this.pid = pid;
		this.name = name;
		this.alias = alias;
		this.description = description;
		this.activated = activated;
		this.flow = flow;
		this.sortOrder = sortOrder;
	}
	public InventoryClosingReportSettingGroupDTO(InventoryClosingReportSettingGroup inventoryClosingReportSettingGroup) {
		super();
		this.pid = inventoryClosingReportSettingGroup.getPid();
		this.name = inventoryClosingReportSettingGroup.getName();
		this.alias = inventoryClosingReportSettingGroup.getAlias();
		this.activated = inventoryClosingReportSettingGroup.isActivated();
		this.flow = inventoryClosingReportSettingGroup.getFlow();
		this.sortOrder = inventoryClosingReportSettingGroup.getSortOrder();
	}
	
	@Override
	public int compareTo(InventoryClosingReportSettingGroupDTO other) {
		return this.sortOrder.compareTo(other.sortOrder);
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

	public boolean isActivated() {
		return activated;
	}

	public void setActivated(boolean activated) {
		this.activated = activated;
	}
	
	public Flow getFlow() {
		return flow;
	}

	public void setFlow(Flow flow) {
		this.flow = flow;
	}

	public Long getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(Long sortOrder) {
		this.sortOrder = sortOrder;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((pid == null) ? 0 : pid.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		InventoryClosingReportSettingGroupDTO other = (InventoryClosingReportSettingGroupDTO) obj;
		if (pid == null) {
			if (other.pid != null)
				return false;
		} else if (!pid.equals(other.pid))
			return false;
		return true;
	}
	
}

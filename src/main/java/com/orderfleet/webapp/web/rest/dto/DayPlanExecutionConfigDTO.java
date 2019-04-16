package com.orderfleet.webapp.web.rest.dto;

import java.util.HashSet;
import java.util.Set;

import com.orderfleet.webapp.domain.DayPlanExecutionConfig;
import com.orderfleet.webapp.domain.enums.DayPlanPages;
import com.orderfleet.webapp.web.rest.api.dto.UserDTO;

/**
 * A DTO for the DayPlanExecutionConfig entity.
 * 
 * @author Muhammed Riyas T
 * @since Jan 03, 2017
 */
public class DayPlanExecutionConfigDTO {

	private Long id;

	private DayPlanPages name;

	private int sortOrder;

	private boolean enabled;

	private Set<UserDTO> users = new HashSet<UserDTO>();

	public DayPlanExecutionConfigDTO() {
		super();
	}

	public DayPlanExecutionConfigDTO(DayPlanExecutionConfig dayPlanExecutionConfig) {
		super();
		this.id = dayPlanExecutionConfig.getId();
		this.name = dayPlanExecutionConfig.getName();
		this.sortOrder = dayPlanExecutionConfig.getSortOrder();
		this.enabled = dayPlanExecutionConfig.getEnabled();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public DayPlanPages getName() {
		return name;
	}

	public void setName(DayPlanPages name) {
		this.name = name;
	}

	public int getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(int sortOrder) {
		this.sortOrder = sortOrder;
	}

	public boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public Set<UserDTO> getUsers() {
		return users;
	}

	public void setUsers(Set<UserDTO> users) {
		this.users = users;
	}

}

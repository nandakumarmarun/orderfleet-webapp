package com.orderfleet.webapp.web.rest.dto;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.orderfleet.webapp.domain.ActivityGroup;

/**
 * A DTO for the ActivityGroup entity.
 * 
 * @author Muhammed Riyas T
 * @since June 09, 2016
 */
public class ActivityGroupDTO {

	private String pid;

	@NotNull
	@Size(min = 1, max = 255)
	private String name;

	@Size(max = 55)
	private String alias;

	private String description;

	private Set<ActivityDTO> activities = new HashSet<ActivityDTO>();

	private boolean activated;

	private LocalDateTime lastModifiedDate;

	public ActivityGroupDTO() {
		super();
	}

	public ActivityGroupDTO(ActivityGroup activityGroup) {
		super();
		this.pid = activityGroup.getPid();
		this.name = activityGroup.getName();
		this.alias = activityGroup.getAlias();
		this.description = activityGroup.getDescription();
		this.lastModifiedDate = activityGroup.getLastModifiedDate();
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

	public Set<ActivityDTO> getActivities() {
		return activities;
	}

	public void setActivities(Set<ActivityDTO> activities) {
		this.activities = activities;
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

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		ActivityGroupDTO activityGroupDTO = (ActivityGroupDTO) o;

		if (!Objects.equals(pid, activityGroupDTO.pid))
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(pid);
	}

	@Override
	public String toString() {
		return "ActivityGroupDTO [pid=" + pid + ", name=" + name + ", alias=" + alias + ", description=" + description
				+ ", activities=" + activities + ", activated=" + activated + "]";
	}
}
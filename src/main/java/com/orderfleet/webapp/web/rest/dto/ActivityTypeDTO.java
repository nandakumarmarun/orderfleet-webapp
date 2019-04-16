package com.orderfleet.webapp.web.rest.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class ActivityTypeDTO {
	
	private String pid;

	@NotNull
	@Size(min = 1, max = 255)
	private String name;

	@Size(max = 55)
	private String alias;

	private String description;

	private boolean activated;

	public ActivityTypeDTO() {
		super();
		// TODO Auto-generated constructor stub
	}

	public ActivityTypeDTO(String pid, String name, String alias, String description, boolean activated) {
		super();
		this.pid = pid;
		this.name = name;
		this.alias = alias;
		this.description = description;
		this.activated = activated;
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

	public boolean getActivated() {
		return activated;
	}

	public void setActivated(boolean activated) {
		this.activated = activated;
	}

	@Override
	public String toString() {
		return "ActivityTypeDTO [pid=" + pid + ", name=" + name + ", alias=" + alias + ", description=" + description
				+ ", activated=" + activated + "]";
	}
	
	
}

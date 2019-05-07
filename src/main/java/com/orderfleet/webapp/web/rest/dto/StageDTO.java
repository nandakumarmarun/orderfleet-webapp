package com.orderfleet.webapp.web.rest.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.orderfleet.webapp.domain.Stage;
import com.orderfleet.webapp.domain.enums.StageNameType;
import com.orderfleet.webapp.domain.enums.StageType;

public class StageDTO {

	private String pid;

	@NotNull
	@Size(min = 1, max = 255)
	private String name;

	@Size(max = 55)
	private String alias;

	private String description;

	private int sortOrder;

	private boolean activated;

	private long target;

	private StageType stageType;

	private StageNameType stageNameType;

	public StageDTO() {
	}

	public StageDTO(Stage stage) {
		super();
		this.pid = stage.getPid();
		this.name = stage.getName();
		this.alias = stage.getAlias();
		this.description = stage.getDescription();
		this.sortOrder = stage.getSortOrder();
		this.activated = stage.getActivated();
		this.stageType = stage.getStageType();
		this.stageNameType = stage.getStageNameType();
		this.target = stage.getTarget();
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

	public int getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(int sortOrder) {
		this.sortOrder = sortOrder;
	}

	public boolean getActivated() {
		return activated;
	}

	public void setActivated(boolean activated) {
		this.activated = activated;
	}

	public long getTarget() {
		return target;
	}

	public void setTarget(long target) {
		this.target = target;
	}

	public StageType getStageType() {
		return stageType;
	}

	public void setStageType(StageType stageType) {
		this.stageType = stageType;
	}

	public StageNameType getStageNameType() {
		return stageNameType;
	}

	public void setStageNameType(StageNameType stageNameType) {
		this.stageNameType = stageNameType;
	}

}
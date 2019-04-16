package com.orderfleet.webapp.web.rest.dto;


import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.orderfleet.webapp.domain.DynamicReportName;

public class DynamicReportUploadDTO {

	private long id;
	
	@NotNull
	@Size(min = 1, max = 255)
	private String name;
	
	private boolean overwrite;

	
	public DynamicReportUploadDTO() {
		super();
		// TODO Auto-generated constructor stub
	}

	public DynamicReportUploadDTO(DynamicReportName dynamicReportUpload) {
		super();
		this.id=dynamicReportUpload.getId();
		this.name = dynamicReportUpload.getName();
		this.overwrite = dynamicReportUpload.isOverwrite();
	}

	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isOverwrite() {
		return overwrite;
	}

	public void setOverwrite(boolean overwrite) {
		this.overwrite = overwrite;
	}

	@Override
	public String toString() {
		return "DynamicReportUploadDTO [id=" + id + ", name=" + name + ", overwrite=" + overwrite + "]";
	}
	
}

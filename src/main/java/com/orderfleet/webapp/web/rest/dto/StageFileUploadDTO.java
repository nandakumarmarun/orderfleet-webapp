package com.orderfleet.webapp.web.rest.dto;

import com.orderfleet.webapp.domain.StageFileUpload;

public class StageFileUploadDTO {
	
	private String pid;

	private String fileUploadName;
	
	private String stageName;
	
	private String stagePid;
	
	public StageFileUploadDTO() {
		super();
	}

	public StageFileUploadDTO(StageFileUpload stageFileUpload) {
		super();
		this.fileUploadName = stageFileUpload.getFileUploadName();
		this.stageName = stageFileUpload.getStage().getName();
		this.stagePid = stageFileUpload.getStage().getPid();
		this.pid = stageFileUpload.getPid();
	}
	
	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public String getFileUploadName() {
		return fileUploadName;
	}
	public void setFileUploadName(String fileUploadName) {
		this.fileUploadName = fileUploadName;
	}

	public String getStageName() {
		return stageName;
	}

	public void setStageName(String stageName) {
		this.stageName = stageName;
	}

	public String getStagePid() {
		return stagePid;
	}

	public void setStagePid(String stagePid) {
		this.stagePid = stagePid;
	}
	
	
}

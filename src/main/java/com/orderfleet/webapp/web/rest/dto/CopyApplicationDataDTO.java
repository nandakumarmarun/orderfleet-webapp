package com.orderfleet.webapp.web.rest.dto;

import java.util.List;

import javax.validation.constraints.NotNull;


public class CopyApplicationDataDTO {

	@NotNull
	private String fromUserPid;
	
	@NotNull
	private List<String> toUsersPid;
	
	@NotNull
	private List<String> dataToCopy;

	public String getFromUserPid() {
		return fromUserPid;
	}

	public void setFromUserPid(String fromUserPid) {
		this.fromUserPid = fromUserPid;
	}

	public List<String> getToUsersPid() {
		return toUsersPid;
	}

	public void setToUsersPid(List<String> toUsersPid) {
		this.toUsersPid = toUsersPid;
	}

	public List<String> getDataToCopy() {
		return dataToCopy;
	}

	public void setDataToCopy(List<String> dataToCopy) {
		this.dataToCopy = dataToCopy;
	}

}

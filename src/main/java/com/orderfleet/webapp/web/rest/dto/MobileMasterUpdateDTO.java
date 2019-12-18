package com.orderfleet.webapp.web.rest.dto;

import java.util.List;

public class MobileMasterUpdateDTO {

	private String userPid;
	private List<MobileMasterDetailDTO> mobileMasterDetailDtos;
	private String updateTime;
	
	public String getUserPid() {
		return userPid;
	}
	public void setUserPid(String userPid) {
		this.userPid = userPid;
	}
	public List<MobileMasterDetailDTO> getMobileMasterDetailDtos() {
		return mobileMasterDetailDtos;
	}
	public void setMobileMasterDetailDtos(List<MobileMasterDetailDTO> mobileMasterDetailDtos) {
		this.mobileMasterDetailDtos = mobileMasterDetailDtos;
	}
	public String getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}
	
	
}

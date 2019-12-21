package com.orderfleet.webapp.web.rest.dto;

import java.util.List;
import java.util.stream.Collectors;

import com.orderfleet.webapp.domain.MobileMasterUpdate;

public class MobileMasterUpdateDTO {

	private String pid;
	private String userPid;
	private List<MobileMasterDetailDTO> mobileMasterDetailDtos;
	private String updateTime;
	private String userBuildVersion;

	public MobileMasterUpdateDTO() {
		super();
	}

	public MobileMasterUpdateDTO(MobileMasterUpdate mobileMasterUpdate) {
		this.pid = mobileMasterUpdate.getPid();
		this.userPid = mobileMasterUpdate.getUser().getPid();
		this.updateTime = mobileMasterUpdate.getUpdateTime() != null ? mobileMasterUpdate.getUpdateTime() : "";
		if (!mobileMasterUpdate.getMobileMasterDetails().isEmpty()) {
			this.mobileMasterDetailDtos = mobileMasterUpdate.getMobileMasterDetails().stream()
					.map(MobileMasterDetailDTO::new).collect(Collectors.toList());
		}
		this.userBuildVersion = mobileMasterUpdate.getUserBuildVersion();
	}

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

	public String getUserBuildVersion() {
		return userBuildVersion;
	}

	public void setUserBuildVersion(String userBuildVersion) {
		this.userBuildVersion = userBuildVersion;
	}

}

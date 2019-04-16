package com.orderfleet.webapp.web.rest.dto;

import com.orderfleet.webapp.domain.UserSourceLocation;

public class UserSourceLocationDTO {

	private Long id;
	
	private String userPid;
	
	private String userName;
	
	private String stockLocationPid;
	
	private String stockLocationName;

	public UserSourceLocationDTO() {
		super();
		// TODO Auto-generated constructor stub
	}

	public UserSourceLocationDTO(Long id, String userPid, String userName, String stockLocationPid,
			String stockLocationName) {
		super();
		this.id = id;
		this.userPid = userPid;
		this.userName = userName;
		this.stockLocationPid = stockLocationPid;
		this.stockLocationName = stockLocationName;
	}
	
	public UserSourceLocationDTO(UserSourceLocation userSourceLocation) {
		super();
		this.id = userSourceLocation.getId();
		this.userPid = userSourceLocation.getUser().getPid();
		this.userName = userSourceLocation.getUser().getFirstName();
		this.stockLocationPid = userSourceLocation.getStockLocation().getPid();
		this.stockLocationName = userSourceLocation.getStockLocation().getName();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUserPid() {
		return userPid;
	}

	public void setUserPid(String userPid) {
		this.userPid = userPid;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getStockLocationPid() {
		return stockLocationPid;
	}

	public void setStockLocationPid(String stockLocationPid) {
		this.stockLocationPid = stockLocationPid;
	}

	public String getStockLocationName() {
		return stockLocationName;
	}

	public void setStockLocationName(String stockLocationName) {
		this.stockLocationName = stockLocationName;
	}

	@Override
	public String toString() {
		return "UserSourceLocationDTO [id=" + id + ", userPid=" + userPid + ", userName=" + userName
				+ ", stockLocationPid=" + stockLocationPid + ", stockLocationName=" + stockLocationName + "]";
	}
	
	
}

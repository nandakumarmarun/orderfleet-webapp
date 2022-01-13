package com.orderfleet.webapp.web.rest.dto;

public class CompanyUserCountDTO {
 private Long companyId;
	private String companyPid;
	private String companyName;
	private int userCount;

	
	public CompanyUserCountDTO() {
		super();
		// TODO Auto-generated constructor stub
	}
	public String getCompanyPid() {
		return companyPid;
	}
	public void setCompanyPid(String companyPid) {
		this.companyPid = companyPid;
	}
	public String getCompanyName() {
		return companyName;
	}
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	public int getUserCount() {
		return userCount;
	}
	public void setUserCount(int userCount) {
		this.userCount = userCount;
	}
	public Long getCompanyId() {
		return companyId;
	}
	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}
	
}

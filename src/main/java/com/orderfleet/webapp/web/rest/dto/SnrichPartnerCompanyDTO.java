package com.orderfleet.webapp.web.rest.dto;

import com.orderfleet.webapp.domain.SnrichPartnerCompany;

public class SnrichPartnerCompanyDTO {

	private Long id;
	private String companyPid;
	private String companyLegalName;
	private String snrichPartnerName;
	private String snrichPartnerPhone;
	private int usersCount;
	private boolean userAdminExist;
	

	public SnrichPartnerCompanyDTO() {
		super();
	}

	public SnrichPartnerCompanyDTO(SnrichPartnerCompany snrichPartnerCompany) {
		super();
		this.id = snrichPartnerCompany.getId();
		this.companyPid = snrichPartnerCompany.getCompany().getPid();
		this.companyLegalName = snrichPartnerCompany.getCompany().getLegalName();
		this.snrichPartnerName = snrichPartnerCompany.getPartner().getName();
		this.snrichPartnerPhone = snrichPartnerCompany.getPartner().getPhone();
		this.usersCount = snrichPartnerCompany.getUsersCount();
	}
	
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getCompanyPid() {
		return companyPid;
	}
	public void setCompanyPid(String companyPid) {
		this.companyPid = companyPid;
	}
	public String getCompanyLegalName() {
		return companyLegalName;
	}
	public void setCompanyLegalName(String companyLegalName) {
		this.companyLegalName = companyLegalName;
	}
	public String getSnrichPartnerName() {
		return snrichPartnerName;
	}
	public void setSnrichPartnerName(String snrichPartnerName) {
		this.snrichPartnerName = snrichPartnerName;
	}
	public String getSnrichPartnerPhone() {
		return snrichPartnerPhone;
	}
	public void setSnrichPartnerPhone(String snrichPartnerPhone) {
		this.snrichPartnerPhone = snrichPartnerPhone;
	}
	public int getUsersCount() {
		return usersCount;
	}
	public void setUsersCount(int usersCount) {
		this.usersCount = usersCount;
	}
	public boolean isUserAdminExist() {
		return userAdminExist;
	}
	public void setUserAdminExist(boolean userAdminExist) {
		this.userAdminExist = userAdminExist;
	}
	
}

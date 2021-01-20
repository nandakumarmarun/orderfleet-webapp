package com.orderfleet.webapp.web.rest.dto;

import com.orderfleet.webapp.domain.BestPerformer;
import com.orderfleet.webapp.domain.Company;

public class BestPerformerUploadDTO {

	private Long id;
	private String pid;
	private byte[] logo;
	private String logoContentType;
	private Company company;
	
	
	public BestPerformerUploadDTO() {
		super();
	}
	public BestPerformerUploadDTO(BestPerformer company) {
		super();
		this.id = company.getId();
		this.pid = company.getPid();
		this.logo = company.getLogo();
		this.logoContentType = company.getLogoContentType();		
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getPid() {
		return pid;
	}
	public void setPid(String pid) {
		this.pid = pid;
	}
	public byte[] getLogo() {
		return logo;
	}
	public void setLogo(byte[] logo) {
		this.logo = logo;
	}
	public Company getCompany() {
		return company;
	}
	public void setCompany(Company company) {
		this.company = company;
	}
	public String getLogoContentType() {
		return logoContentType;
	}
	public void setLogoContentType(String logoContentType) {
		this.logoContentType = logoContentType;
	}
	@Override
	public String toString() {
		return "BestPerformerUploadDTO [id=" + id + ", pid=" + pid + ", logoContentType=" + logoContentType + "]";
	}
}

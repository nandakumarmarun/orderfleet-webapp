package com.orderfleet.webapp.web.rest.dto;

import java.util.List;

public class UpdateQntyIvhdDTO {

	private String pid;
	private List<UpdateIvhdetailQnty> ivhd ;
	
	public String getPid() {
		return pid;
	}
	public void setPid(String pid) {
		this.pid = pid;
	}
	public List<UpdateIvhdetailQnty> getIvhd() {
		return ivhd;
	}
	public void setIvhd(List<UpdateIvhdetailQnty> ivhd) {
		this.ivhd = ivhd;
	}
	
	@Override
	public String toString() {
		return "UpdateQntyIvhdDTO [pid=" + pid + ", ivhd=" + ivhd + "]";
	}
	
}

package com.orderfleet.webapp.web.rest.api.dto;

import com.orderfleet.webapp.domain.DistributorDealerAssociation;

public class DistributorDealerDTO {
	
	private String pid;
	
	private String distributorPid;
	
	private String dealerPid;

	public DistributorDealerDTO() {
		super();
		// TODO Auto-generated constructor stub
	}

	
	public DistributorDealerDTO(DistributorDealerAssociation distributorDealerAssociation)
	{
		super();
		this.pid=distributorDealerAssociation.getPid();
		this.dealerPid = distributorDealerAssociation.getDealer().getPid();
		this.distributorPid = distributorDealerAssociation.getDistributor().getPid(); 
	}
	public DistributorDealerDTO(String pid, String distributorPid, String dealerPid) {
		super();
		this.pid = pid;
		this.distributorPid = distributorPid;
		this.dealerPid = dealerPid;
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public String getDistributorPid() {
		return distributorPid;
	}

	public void setDistributorPid(String distributorPid) {
		this.distributorPid = distributorPid;
	}

	public String getDealerPid() {
		return dealerPid;
	}

	public void setDealerPid(String dealerPid) {
		this.dealerPid = dealerPid;
	}

	@Override
	public String toString() {
		return "DistributorDealerDTO [pid=" + pid + ", distributorPid=" + distributorPid + ", dealerPid=" + dealerPid
				+ "]";
	}
	
	
	

}

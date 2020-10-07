package com.orderfleet.webapp.web.rest.api.dto;

import com.orderfleet.webapp.domain.PostDatedVoucherAllocation;

public class PostDatedVoucherAllocationDTO {

	private Long id;

	private String pid;
	
	private String allocReferenceVoucher;
	
	private double allocReferenceVoucherAmount;
	
	private String postDatedVoucherPid;
	
	private String voucherNumber;

	public PostDatedVoucherAllocationDTO(){
		
	}
	public PostDatedVoucherAllocationDTO(PostDatedVoucherAllocationDTO dto){
		this.allocReferenceVoucher = dto.getAllocReferenceVoucher();
		this.allocReferenceVoucherAmount = dto.getAllocReferenceVoucherAmount();
	}
	
	public PostDatedVoucherAllocationDTO(PostDatedVoucherAllocation postDatedVoucherAllocation){
		this.allocReferenceVoucher = postDatedVoucherAllocation.getAllocReferenceVoucher();
		this.allocReferenceVoucherAmount = postDatedVoucherAllocation.getAllocReferenceVoucherAmount();
		this.postDatedVoucherPid = postDatedVoucherAllocation.getPostDatedVoucher().getPid();
		this.pid = postDatedVoucherAllocation.getPid();
		this.voucherNumber = postDatedVoucherAllocation.getVoucherNumber();
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
	public String getAllocReferenceVoucher() {
		return allocReferenceVoucher;
	}

	public void setAllocReferenceVoucher(String allocReferenceVoucher) {
		this.allocReferenceVoucher = allocReferenceVoucher;
	}

	public double getAllocReferenceVoucherAmount() {
		return allocReferenceVoucherAmount;
	}

	public void setAllocReferenceVoucherAmount(double allocReferenceVoucherAmount) {
		this.allocReferenceVoucherAmount = allocReferenceVoucherAmount;
	}
	public String getPostDatedVoucherPid() {
		return postDatedVoucherPid;
	}
	public void setPostDatedVoucherPid(String postDatedVoucherPid) {
		this.postDatedVoucherPid = postDatedVoucherPid;
	}
	public String getVoucherNumber() {
		return voucherNumber;
	}
	public void setVoucherNumber(String voucherNumber) {
		this.voucherNumber = voucherNumber;
	}
	
	
	
	
}
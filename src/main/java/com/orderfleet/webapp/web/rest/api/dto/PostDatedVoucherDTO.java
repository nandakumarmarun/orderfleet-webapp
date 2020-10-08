package com.orderfleet.webapp.web.rest.api.dto;



import java.util.List;
import java.util.stream.Collectors;

import com.orderfleet.webapp.domain.PostDatedVoucher;

/**
 * @author Anish
 * @since September 3, 2018
 *
 */
public class PostDatedVoucherDTO {

	private Long id;

	private String pid;

	private String accountProfilePid;

	private String accountProfileName;

	private String referenceVoucher;

	private String referenceDocumentNumber;

	private String referenceDocumentDate;

	private double referenceDocumentAmount;

	private String remark;
	
	private List<PostDatedVoucherAllocationDTO> postDatedVoucherAllocationList;

	public PostDatedVoucherDTO() {
		super();
	}

	


	public PostDatedVoucherDTO(PostDatedVoucher postDatedVoucher) {
		super();
		this.id = postDatedVoucher.getId();
		this.pid = postDatedVoucher.getPid();
		this.accountProfilePid = postDatedVoucher.getAccountProfile().getPid();
		this.accountProfileName = postDatedVoucher.getAccountProfile().getName();
		
		this.referenceVoucher = postDatedVoucher.getReceivableBillNumber();
		this.referenceDocumentNumber = postDatedVoucher.getReferenceDocumentNumber();
		this.referenceDocumentDate = postDatedVoucher.getReferenceDocumentDate().toString();
		this.referenceDocumentAmount = postDatedVoucher.getReferenceDocumentAmount();
		this.remark = postDatedVoucher.getRemark();
		if(postDatedVoucher.getPostDatedVoucherAllocation() != null){
			this.postDatedVoucherAllocationList = postDatedVoucher.getPostDatedVoucherAllocation()
					.stream().map(pdcAlloc -> new PostDatedVoucherAllocationDTO(pdcAlloc)).collect(Collectors.toList());
		}
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

	public String getAccountProfilePid() {
		return accountProfilePid;
	}

	public void setAccountProfilePid(String accountProfilePid) {
		this.accountProfilePid = accountProfilePid;
	}

	public String getAccountProfileName() {
		return accountProfileName;
	}

	public void setAccountProfileName(String accountProfileName) {
		this.accountProfileName = accountProfileName;
	}


	public String getReferenceVoucher() {
		return referenceVoucher;
	}

	public void setReferenceVoucher(String referenceVoucher) {
		this.referenceVoucher = referenceVoucher;
	}

	public String getReferenceDocumentNumber() {
		return referenceDocumentNumber;
	}

	public void setReferenceDocumentNumber(String referenceDocumentNumber) {
		this.referenceDocumentNumber = referenceDocumentNumber;
	}

	public String getReferenceDocumentDate() {
		return referenceDocumentDate;
	}

	public void setReferenceDocumentDate(String referenceDocumentDate) {
		this.referenceDocumentDate = referenceDocumentDate;
	}

	public double getReferenceDocumentAmount() {
		return referenceDocumentAmount;
	}

	public void setReferenceDocumentAmount(double referenceDocumentAmount) {
		this.referenceDocumentAmount = referenceDocumentAmount;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public List<PostDatedVoucherAllocationDTO> getPostDatedVoucherAllocationList() {
		return postDatedVoucherAllocationList;
	}

	public void setPostDatedVoucherAllocationList(List<PostDatedVoucherAllocationDTO> postDatedVoucherAllocationList) {
		this.postDatedVoucherAllocationList = postDatedVoucherAllocationList;
	}
	
}

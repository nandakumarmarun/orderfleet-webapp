package com.orderfleet.webapp.web.rest.dto;

import com.orderfleet.webapp.domain.VoucherNumberGenerator;

public class VoucherNumberGeneratorDTO {

	private Long id;
	
	private String userPid;
	
	private String userName;
	
	private String documentPid;
	
	private String documentName;
	
	private String prefix;
	
	private long startWith;
	
	private String lastVoucherNumber;

	
	
	public VoucherNumberGeneratorDTO() {
		super();
		// TODO Auto-generated constructor stub
	}

	
	public VoucherNumberGeneratorDTO(VoucherNumberGenerator voucherNumberGenerator) {
		super();
		this.id = voucherNumberGenerator.getId();
		this.userPid = voucherNumberGenerator.getUser().getPid();
		this.userName=voucherNumberGenerator.getUser().getFirstName();
		this.documentPid = voucherNumberGenerator.getDocument().getPid();
		this.documentName = voucherNumberGenerator.getDocument().getName();
		this.prefix = voucherNumberGenerator.getPrefix();
		this.startWith = voucherNumberGenerator.getStartwith();
		//this.lastVoucherNumber = voucherNumberGenerator.getStartwith();
	}


	public VoucherNumberGeneratorDTO(Long id, String userPid,String userName, String documentPid, String documentName, String prefix,
			long startWith,String lastVoucherNumber) {
		super();
		this.id = id;
		this.userPid = userPid;
		this.userName=userName;
		this.documentPid = documentPid;
		this.documentName = documentName;
		this.prefix = prefix;
		this.startWith = startWith;
		this.lastVoucherNumber = lastVoucherNumber;
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


	public String getDocumentPid() {
		return documentPid;
	}


	public void setDocumentPid(String documentPid) {
		this.documentPid = documentPid;
	}


	public String getDocumentName() {
		return documentName;
	}


	public void setDocumentName(String documentName) {
		this.documentName = documentName;
	}


	public String getPrefix() {
		return prefix;
	}


	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}


	public long getStartWith() {
		return startWith;
	}


	public void setStartWith(long startWith) {
		this.startWith = startWith;
	}


	public String getLastVoucherNumber() {
		return lastVoucherNumber;
	}


	public void setLastVoucherNumber(String lastVoucherNumber) {
		this.lastVoucherNumber = lastVoucherNumber;
	}


	@Override
	public String toString() {
		return "VoucherNumberGeneratorDTO [id=" + id + ", userPid=" + userPid + ", userName=" + userName
				+ ", documentPid=" + documentPid + ", documentName=" + documentName + ", prefix=" + prefix
				+ ", startWith=" + startWith + ", lastVoucherNumber=" + lastVoucherNumber + "]";
	}

	
	
}

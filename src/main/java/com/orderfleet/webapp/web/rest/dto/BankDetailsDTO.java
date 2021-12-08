package com.orderfleet.webapp.web.rest.dto;

import java.time.LocalDateTime;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import com.orderfleet.webapp.domain.AccountType;
import com.orderfleet.webapp.domain.enums.AccountNameType;
import com.orderfleet.webapp.domain.enums.ReceiverSupplierType;

/**
 * A DTO for the AccountType entity.
 * 
 * @author Muhammed Riyas T
 * @since May 14, 2016
 */
public class BankDetailsDTO {

	private Long id;

	private String pid;

	private String bankName;

	private String accountNumber;

	private String accountHolderName;

	private String ifscCode;

	private String swiftCode;

	private String phoneNumber;

	private String companyPid;

	private String branch;

	private byte[] qrImage;

	private String qrImageContentType;

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

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public String getAccountHolderName() {
		return accountHolderName;
	}

	public void setAccountHolderName(String accountHolderName) {
		this.accountHolderName = accountHolderName;
	}

	public String getIfscCode() {
		return ifscCode;
	}

	public void setIfscCode(String ifscCode) {
		this.ifscCode = ifscCode;
	}

	public String getSwiftCode() {
		return swiftCode;
	}

	public void setSwiftCode(String swiftCode) {
		this.swiftCode = swiftCode;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getCompanyPid() {
		return companyPid;
	}

	public void setCompanyPid(String companyPid) {
		this.companyPid = companyPid;
	}

	public String getBranch() {
		return branch;
	}

	public void setBranch(String branch) {
		this.branch = branch;
	}

	public byte[] getQrImage() {
		return qrImage;
	}

	public void setQrImage(byte[] qrImage) {
		this.qrImage = qrImage;
	}

	public String getQrImageContentType() {
		return qrImageContentType;
	}

	public void setQrImageContentType(String qrImageContentType) {
		this.qrImageContentType = qrImageContentType;
	}

}

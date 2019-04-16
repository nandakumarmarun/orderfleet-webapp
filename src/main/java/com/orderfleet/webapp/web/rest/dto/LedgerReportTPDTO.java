package com.orderfleet.webapp.web.rest.dto;

import java.time.LocalDate;

import com.orderfleet.webapp.domain.enums.DebitCredit;

/**
 * A DTO for the LedgerReportTP entity.
 * 
 * @author Sarath
 * @since Nov 2, 2016
 */
public class LedgerReportTPDTO {

	private Long id;
	private String voucheNo;
	private LocalDate voucherDate;
	private String narration;
	private Double amount;
	private String type;
	private DebitCredit debitCredit;
	private String divisionName;
	private String divisionAlias;
	private String divisionPid;
	private String accountProfilePid;
	private String accountProfileName;

	public LedgerReportTPDTO() {
		super();
	}

	public LedgerReportTPDTO(Long id, String voucheNo, LocalDate voucherDate, String narration, Double amount,
			String type, DebitCredit debitCredit, String divisionName, String divisionAlias, String divisionPid,
			String accountProfilePid, String accountProfileName) {
		super();
		this.id = id;
		this.voucheNo = voucheNo;
		this.voucherDate = voucherDate;
		this.narration = narration;
		this.amount = amount;
		this.type = type;
		this.debitCredit = debitCredit;
		this.divisionName = divisionName;
		this.divisionAlias = divisionAlias;
		this.divisionPid = divisionPid;
		this.accountProfilePid = accountProfilePid;
		this.accountProfileName = accountProfileName;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getVoucheNo() {
		return voucheNo;
	}

	public void setVoucheNo(String voucheNo) {
		this.voucheNo = voucheNo;
	}

	public LocalDate getVoucherDate() {
		return voucherDate;
	}

	public void setVoucherDate(LocalDate voucherDate) {
		this.voucherDate = voucherDate;
	}

	public String getNarration() {
		return narration;
	}

	public void setNarration(String narration) {
		this.narration = narration;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public DebitCredit getDebitCredit() {
		return debitCredit;
	}

	public void setDebitCredit(DebitCredit debitCredit) {
		this.debitCredit = debitCredit;
	}

	public String getDivisionName() {
		return divisionName;
	}

	public void setDivisionName(String divisionName) {
		this.divisionName = divisionName;
	}

	public String getDivisionPid() {
		return divisionPid;
	}

	public void setDivisionPid(String divisionPid) {
		this.divisionPid = divisionPid;
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

	public String getDivisionAlias() {
		return divisionAlias;
	}

	public void setDivisionAlias(String divisionAlias) {
		this.divisionAlias = divisionAlias;
	}

}

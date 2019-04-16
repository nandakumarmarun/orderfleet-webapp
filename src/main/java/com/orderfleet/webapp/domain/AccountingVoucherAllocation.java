package com.orderfleet.webapp.domain;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import com.orderfleet.webapp.domain.enums.PaymentMode;

/**
 * A AccountingVoucherAllocation.
 * 
 * @author Muhammed Riyas T
 * @since July 27, 2016
 */
@Entity
@Table(name = "tbl_accounting_voucher_allocation")
public class AccountingVoucherAllocation implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GenericGenerator(name = "seq_accounting_voucher_allocation_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "seq_accounting_voucher_allocation_id") })
	@GeneratedValue(generator = "seq_accounting_voucher_allocation_id_GEN")
	@Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_accounting_voucher_allocation_id')")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "accounting_voucher_detail_id")
	private AccountingVoucherDetail accountingVoucherDetail;

	@Column(name = "receivable_payable_pid")
	private String receivablePayablePid;

	@Column(name = "voucher_number")
	private String voucherNumber;

	@Column(name = "reference_number")
	private String referenceNumber;

	@Column(name = "reference_document_number")
	private String referenceDocumentNumber;

	@Column(name = "mode")
	private PaymentMode mode;

	@Column(name = "amount")
	private double amount;

	@Column(name = "remarks", length = 1000)
	private String remarks;

	public AccountingVoucherAllocation() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public AccountingVoucherDetail getAccountingVoucherDetail() {
		return accountingVoucherDetail;
	}

	public void setAccountingVoucherDetail(AccountingVoucherDetail accountingVoucherDetail) {
		this.accountingVoucherDetail = accountingVoucherDetail;
	}

	public String getReceivablePayablePid() {
		return receivablePayablePid;
	}

	public void setReceivablePayablePid(String receivablePayablePid) {
		this.receivablePayablePid = receivablePayablePid;
	}

	public String getVoucherNumber() {
		return voucherNumber;
	}

	public void setVoucherNumber(String voucherNumber) {
		this.voucherNumber = voucherNumber;
	}

	public String getReferenceNumber() {
		return referenceNumber;
	}

	public void setReferenceNumber(String referenceNumber) {
		this.referenceNumber = referenceNumber;
	}

	public String getReferenceDocumentNumber() {
		return referenceDocumentNumber;
	}

	public void setReferenceDocumentNumber(String referenceDocumentNumber) {
		this.referenceDocumentNumber = referenceDocumentNumber;
	}

	public PaymentMode getMode() {
		return mode;
	}

	public void setMode(PaymentMode mode) {
		this.mode = mode;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

}

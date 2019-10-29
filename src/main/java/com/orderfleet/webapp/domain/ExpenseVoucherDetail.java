package com.orderfleet.webapp.domain;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import com.orderfleet.webapp.domain.enums.PaymentMode;

/**
 * A ExpenseVoucherDetail.
 * 
 * @author Prashob Sasidharan
 * @since October 29, 2019
 */
//@Entity
//@Table(name = "tbl_expense_voucher_detail")
public class ExpenseVoucherDetail implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GenericGenerator(name = "seq_expense_voucher_detail_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "seq_expense_voucher_detail_id") })
	@GeneratedValue(generator = "seq_expense_voucher_detail_id_GEN")
	@Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_expense_voucher_detail_id')")
	private Long id;

	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name = "expense_voucher_header_id")
	private ExpenseVoucherHeader expenseVoucherHeader;

	@Column(name = "mode")
	private PaymentMode mode;

	@Column(name = "amount")
	private double amount;

	@Column(name = "instrument_number")
	private String instrumentNumber;

	@Column(name = "instrument_date")
	private LocalDateTime instrumentDate;

	@Column(name = "bank_name")
	private String bankName;

	@ManyToOne
	private Bank bank;

	@ManyToOne
	private AccountProfile by;

	@ManyToOne
	private AccountProfile to;

	@Column(name = "voucher_number")
	private String voucherNumber;

	@Column(name = "voucher_date")
	private LocalDateTime voucherDate;

	@Column(name = "reference_number")
	private String referenceNumber;

	@Column(name = "remarks", length = 1000)
	private String remarks;

	private String provisionalReceiptNo;

	@ManyToOne
	private IncomeExpenseHead incomeExpenseHead;

	public ExpenseVoucherDetail() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public ExpenseVoucherHeader getExpenseVoucherHeader() {
		return expenseVoucherHeader;
	}

	public void setExpenseVoucherHeader(ExpenseVoucherHeader expenseVoucherHeader) {
		this.expenseVoucherHeader = expenseVoucherHeader;
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

	public String getInstrumentNumber() {
		return instrumentNumber;
	}

	public void setInstrumentNumber(String instrumentNumber) {
		this.instrumentNumber = instrumentNumber;
	}

	public LocalDateTime getInstrumentDate() {
		return instrumentDate;
	}

	public void setInstrumentDate(LocalDateTime instrumentDate) {
		this.instrumentDate = instrumentDate;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public Bank getBank() {
		return bank;
	}

	public void setBank(Bank bank) {
		this.bank = bank;
	}

	public AccountProfile getBy() {
		return by;
	}

	public void setBy(AccountProfile by) {
		this.by = by;
	}

	public AccountProfile getTo() {
		return to;
	}

	public void setTo(AccountProfile to) {
		this.to = to;
	}

	public String getVoucherNumber() {
		return voucherNumber;
	}

	public void setVoucherNumber(String voucherNumber) {
		this.voucherNumber = voucherNumber;
	}

	public LocalDateTime getVoucherDate() {
		return voucherDate;
	}

	public void setVoucherDate(LocalDateTime voucherDate) {
		this.voucherDate = voucherDate;
	}

	public String getReferenceNumber() {
		return referenceNumber;
	}

	public void setReferenceNumber(String referenceNumber) {
		this.referenceNumber = referenceNumber;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getProvisionalReceiptNo() {
		return provisionalReceiptNo;
	}

	public void setProvisionalReceiptNo(String provisionalReceiptNo) {
		this.provisionalReceiptNo = provisionalReceiptNo;
	}

	public IncomeExpenseHead getIncomeExpenseHead() {
		return incomeExpenseHead;
	}

	public void setIncomeExpenseHead(IncomeExpenseHead incomeExpenseHead) {
		this.incomeExpenseHead = incomeExpenseHead;
	}

	@Override
	public String toString() {
		return "ExpenseVoucherDetail [mode=" + mode + ", amount=" + amount + ", instrumentNumber=" + instrumentNumber
				+ ", instrumentDate=" + instrumentDate + ", bankName=" + bankName + ", bank=" + bank + ", by=" + by
				+ ", to=" + to + ", voucherNumber=" + voucherNumber + ", voucherDate=" + voucherDate
				+ ", referenceNumber=" + referenceNumber + ", remarks=" + remarks + "]";
	}

}

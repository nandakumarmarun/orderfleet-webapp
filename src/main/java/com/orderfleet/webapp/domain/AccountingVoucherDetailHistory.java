package com.orderfleet.webapp.domain;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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
 * A AccountingVoucherDetailHistory.
 * 
 * @author Muhammed Riyas T
 * @since October 24, 2016
 */
@Entity
@Table(name = "tbl_accounting_voucher_detail_history")
public class AccountingVoucherDetailHistory implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GenericGenerator(name = "seq_accounting_voucher_detail_history_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "seq_accounting_voucher_detail_history_id") })
	@GeneratedValue(generator = "seq_accounting_voucher_detail_history_id_GEN")
	@Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_accounting_voucher_detail_history_id')")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "accounting_voucher_header_history_id")
	private AccountingVoucherHeaderHistory accountingVoucherHeaderHistory;

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

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "accounting_voucher_detail_history_id")
	private List<AccountingVoucherAllocationHistory> accountingVoucherAllocations;

	@ManyToOne
	private IncomeExpenseHead incomeExpenseHead;

	public AccountingVoucherDetailHistory() {
	}

	public AccountingVoucherDetailHistory(AccountingVoucherDetail accountingVoucherDetail) {
		super();
		this.mode = accountingVoucherDetail.getMode();
		this.amount = accountingVoucherDetail.getAmount();
		this.instrumentNumber = accountingVoucherDetail.getInstrumentNumber();
		this.instrumentDate = accountingVoucherDetail.getInstrumentDate();
		this.bankName = accountingVoucherDetail.getBankName();
		this.bank = accountingVoucherDetail.getBank();
		this.by = accountingVoucherDetail.getBy();
		this.to = accountingVoucherDetail.getTo();
		this.voucherNumber = accountingVoucherDetail.getVoucherNumber();
		this.voucherDate = accountingVoucherDetail.getVoucherDate();
		this.referenceNumber = accountingVoucherDetail.getReferenceNumber();
		this.remarks = accountingVoucherDetail.getRemarks();
		this.accountingVoucherAllocations = accountingVoucherDetail.getAccountingVoucherAllocations().stream()
				.map(AccountingVoucherAllocationHistory::new).collect(Collectors.toList());
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public AccountingVoucherHeaderHistory getAccountingVoucherHeaderHistory() {
		return accountingVoucherHeaderHistory;
	}

	public void setAccountingVoucherHeaderHistory(AccountingVoucherHeaderHistory accountingVoucherHeaderHistory) {
		this.accountingVoucherHeaderHistory = accountingVoucherHeaderHistory;
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

	public List<AccountingVoucherAllocationHistory> getAccountingVoucherAllocations() {
		return accountingVoucherAllocations;
	}

	public void setAccountingVoucherAllocations(List<AccountingVoucherAllocationHistory> accountingVoucherAllocations) {
		this.accountingVoucherAllocations = accountingVoucherAllocations;
	}

	public IncomeExpenseHead getIncomeExpenseHead() {
		return incomeExpenseHead;
	}

	public void setIncomeExpenseHead(IncomeExpenseHead incomeExpenseHead) {
		this.incomeExpenseHead = incomeExpenseHead;
	}

}

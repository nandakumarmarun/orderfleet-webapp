package com.orderfleet.webapp.domain;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import com.orderfleet.webapp.domain.enums.DebitCredit;
import com.orderfleet.webapp.domain.enums.PaymentMode;

@Entity
@Table(name = "tbl_financial_closing_detail_inventory")
public class FinancialClosingDetailInventory implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GenericGenerator(name = "seq_financial_closing_detail_inventory_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "seq_financial_closing_detail_inventory_id") })
	@GeneratedValue(generator = "seq_financial_closing_detail_inventory_id_GEN")
	@Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_financial_closing_detail_inventory_id')")
	private Long id;
	
	@NotNull
	private FinancialClosingHeader financialClosingHeader;
	
	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "payment_mode", nullable = false)
	private PaymentMode paymentMode;
	
	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "debit_credit", nullable = false)
	private DebitCredit debitCredit;

	@NotNull
	private LocalDateTime createdDate = LocalDateTime.now();
	
	@NotNull
	private User createdBy;
	
	@NotNull
	@ManyToOne
	private InventoryVoucherHeader inventoryVoucherHeader;
	
	@NotNull
	@ManyToOne
	private Company company;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public FinancialClosingHeader getFinancialClosingHeader() {
		return financialClosingHeader;
	}

	public void setFinancialClosingHeader(FinancialClosingHeader financialClosingHeader) {
		this.financialClosingHeader = financialClosingHeader;
	}

	public PaymentMode getPaymentMode() {
		return paymentMode;
	}

	public void setPaymentMode(PaymentMode paymentMode) {
		this.paymentMode = paymentMode;
	}

	public DebitCredit getDebitCredit() {
		return debitCredit;
	}

	public void setDebitCredit(DebitCredit debitCredit) {
		this.debitCredit = debitCredit;
	}

	public LocalDateTime getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(LocalDateTime createdDate) {
		this.createdDate = createdDate;
	}

	public User getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(User createdBy) {
		this.createdBy = createdBy;
	}

	public InventoryVoucherHeader getInventoryVoucherHeader() {
		return inventoryVoucherHeader;
	}

	public void setInventoryVoucherHeader(InventoryVoucherHeader inventoryVoucherHeader) {
		this.inventoryVoucherHeader = inventoryVoucherHeader;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

}

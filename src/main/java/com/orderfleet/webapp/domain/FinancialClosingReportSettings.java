package com.orderfleet.webapp.domain;

import java.io.Serializable;

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
@Table(name = "tbl_financial_closing_report_settings")
public class FinancialClosingReportSettings implements Serializable{

	private static final long serialVersionUID = 1L;

	@Id
	@GenericGenerator(name = "seq_financial_closing_report_settings_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "seq_financial_closing_report_settings_id") })
	@GeneratedValue(generator = "seq_financial_closing_report_settings_id_GEN")
	@Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_financial_closing_report_settings_id')")
	private Long id;
	
	@NotNull
	@ManyToOne
	private Company company;
	
	@NotNull
	@ManyToOne
	private Document document;
	
	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "debit_credit", nullable = false)
	private DebitCredit debitCredit;
	
	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "payment_mode", nullable = false)
	private PaymentMode paymentMode;
	
	@Column(name = "sort_order")
	private Long sortOrder;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public Document getDocument() {
		return document;
	}

	public void setDocument(Document document) {
		this.document = document;
	}

	public DebitCredit getDebitCredit() {
		return debitCredit;
	}

	public void setDebitCredit(DebitCredit debitCredit) {
		this.debitCredit = debitCredit;
	}

	public PaymentMode getPaymentMode() {
		return paymentMode;
	}

	public void setPaymentMode(PaymentMode paymentMode) {
		this.paymentMode = paymentMode;
	}

	public Long getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(Long sortOrder) {
		this.sortOrder = sortOrder;
	}

}

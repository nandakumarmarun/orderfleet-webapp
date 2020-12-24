package com.orderfleet.webapp.domain;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.orderfleet.webapp.domain.enums.ReceivablePayableType;

/**
 * A ReceivablePayable
 * 
 * @author Sarath
 * @since Aug 16, 2016
 */
@Entity
@Table(name = "tbl_receivable_payable")
public class ReceivablePayable implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GenericGenerator(name = "seq_receivable_payable_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "seq_receivable_payable_id") })
	@GeneratedValue(generator = "seq_receivable_payable_id_GEN")
	@Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_receivable_payable_id')")
	private Long id;

	@NotNull
	@Column(name = "pid", unique = true, nullable = false, updatable = false)
	private String pid;

	@NotNull
	@ManyToOne
	@JoinColumn(name = "account_profile")
	private AccountProfile accountProfile;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "receivable_payable_type", nullable = false)
	private ReceivablePayableType receivablePayableType;

	@Column(name = "reference_document_number")
	private String referenceDocumentNumber;

	@Column(name = "reference_document_date")
	private LocalDate referenceDocumentDate;

	@Column(name = "reference_document_type")
	private String referenceDocumentType;

	@Column(name = "reference_document_amount")
	private double referenceDocumentAmount;

	@Column(name = "reference_document_balance_amount")
	private double referenceDocumentBalanceAmount;

	@Column(name = "remarks")
	private String remarks;

	@Column(name = "BILL_OVER_DUE")
	private Long billOverDue;

	@ManyToOne
	private Company company;

	@Column(name = "created_date", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	@JsonIgnore
	private LocalDateTime createdDate = LocalDateTime.now();

	@Column(name = "last_modified_date", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	@JsonIgnore
	private LocalDateTime lastModifiedDate = LocalDateTime.now();

	@ManyToOne
	@JoinColumn(name = "supplier_account_profile")
	private AccountProfile supplierAccountProfile;

	@PreUpdate
	public void preUpdate() {
		this.lastModifiedDate = LocalDateTime.now();
	}

	public ReceivablePayable() {
		super();
	}

	public ReceivablePayable(Long id, String pid, AccountProfile accountProfile,
			ReceivablePayableType receivablePayableType, String referenceDocumentNumber,
			LocalDate referenceDocumentDate, String referenceDocumentType, double referenceDocumentAmount,
			double referenceDocumentBalanceAmount, String remarks, Long billOverDue, Company company) {
		super();
		this.id = id;
		this.pid = pid;
		this.accountProfile = accountProfile;
		this.receivablePayableType = receivablePayableType;
		this.referenceDocumentNumber = referenceDocumentNumber;
		this.referenceDocumentDate = referenceDocumentDate;
		this.referenceDocumentType = referenceDocumentType;
		this.referenceDocumentAmount = referenceDocumentAmount;
		this.referenceDocumentBalanceAmount = referenceDocumentBalanceAmount;
		this.remarks = remarks;
		this.billOverDue = billOverDue;
		this.company = company;
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

	public AccountProfile getAccountProfile() {
		return accountProfile;
	}

	public void setAccountProfile(AccountProfile accountProfile) {
		this.accountProfile = accountProfile;
	}

	public ReceivablePayableType getReceivablePayableType() {
		return receivablePayableType;
	}

	public void setReceivablePayableType(ReceivablePayableType receivablePayableType) {
		this.receivablePayableType = receivablePayableType;
	}

	public String getReferenceDocumentNumber() {
		return referenceDocumentNumber;
	}

	public void setReferenceDocumentNumber(String referenceDocumentNumber) {
		this.referenceDocumentNumber = referenceDocumentNumber;
	}

	public LocalDate getReferenceDocumentDate() {
		return referenceDocumentDate;
	}

	public void setReferenceDocumentDate(LocalDate referenceDocumentDate) {
		this.referenceDocumentDate = referenceDocumentDate;
	}

	public String getReferenceDocumentType() {
		return referenceDocumentType;
	}

	public void setReferenceDocumentType(String referenceDocumentType) {
		this.referenceDocumentType = referenceDocumentType;
	}

	public double getReferenceDocumentAmount() {
		return referenceDocumentAmount;
	}

	public void setReferenceDocumentAmount(double referenceDocumentAmount) {
		this.referenceDocumentAmount = referenceDocumentAmount;
	}

	public double getReferenceDocumentBalanceAmount() {
		return referenceDocumentBalanceAmount;
	}

	public void setReferenceDocumentBalanceAmount(double referenceDocumentBalanceAmount) {
		this.referenceDocumentBalanceAmount = referenceDocumentBalanceAmount;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public Long getBillOverDue() {
		return billOverDue;
	}

	public void setBillOverDue(Long billOverDue) {
		this.billOverDue = billOverDue;
	}

	public LocalDateTime getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(LocalDateTime createdDate) {
		this.createdDate = createdDate;
	}

	public void setLastModifiedDate(LocalDateTime lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}

	public LocalDateTime getLastModifiedDate() {
		return lastModifiedDate;
	}

	public AccountProfile getSupplierAccountProfile() {
		return supplierAccountProfile;
	}

	public void setSupplierAccountProfile(AccountProfile supplierAccountProfile) {
		this.supplierAccountProfile = supplierAccountProfile;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		ReceivablePayable receivablePayable = (ReceivablePayable) o;
		if (receivablePayable.id == null || id == null) {
			return false;
		}
		return Objects.equals(id, receivablePayable.id);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(id);
	}

}

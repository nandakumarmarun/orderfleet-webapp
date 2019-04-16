
package com.orderfleet.webapp.domain;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
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

/**
 * @author Anish
 * @since September 3, 2018
 *
 */
@Entity
@Table(name = "tbl_post_dated_voucher")
public class PostDatedVoucher implements Serializable,Cloneable{

	private static final long serialVersionUID = 1L;

	@Id
	@GenericGenerator(name = "seq_post_dated_voucher_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "seq_post_dated_voucher_id") })
	@GeneratedValue(generator = "seq_post_dated_voucher_id_GEN")
	@Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_post_dated_voucher_id')")
	private Long id;

	@NotNull
	@Column(name = "pid", unique = true, nullable = false, updatable = false)
	private String pid;

	@NotNull
	@ManyToOne
	@JoinColumn(name = "account_profile")
	private AccountProfile accountProfile;
	
	@Column(name = "receivable_bill_number")
	private String receivableBillNumber;
	
	//ref number for the pdc document
	@Column(name = "reference_document_number")
	private String referenceDocumentNumber;

	@Column(name = "reference_document_date")
	private LocalDate referenceDocumentDate;

	@Column(name = "reference_document_amount")
	private double referenceDocumentAmount;
	
	@Column(name = "remarks")
	private String remark;
	
	@ManyToOne
	private Company company;

	@Column(name = "created_date", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	@JsonIgnore
	private LocalDateTime createdDate = LocalDateTime.now();

	@Column(name = "last_modified_date", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	@JsonIgnore
	private LocalDateTime lastModifiedDate = LocalDateTime.now();
	
	@PreUpdate
	public void preUpdate() {
		this.lastModifiedDate = LocalDateTime.now();
	}

	public PostDatedVoucher() {
		super();
	}

	public PostDatedVoucher(Long id, String pid, AccountProfile accountProfile,
			String receivableBillNumber, String referenceDocumentNumber, LocalDate referenceDocumentDate,
			double referenceDocumentAmount, String remark, Company company) {
		super();
		this.id = id;
		this.pid = pid;
		this.accountProfile = accountProfile;
		this.receivableBillNumber = receivableBillNumber;
		this.referenceDocumentNumber = referenceDocumentNumber;
		this.referenceDocumentDate = referenceDocumentDate;
		this.referenceDocumentAmount = referenceDocumentAmount;
		this.remark = remark;
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

	public String getReceivableBillNumber() {
		return receivableBillNumber;
	}

	public void setReceivableBillNumber(String receivableBillNumber) {
		this.receivableBillNumber = receivableBillNumber;
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

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public LocalDateTime getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(LocalDateTime createdDate) {
		this.createdDate = createdDate;
	}

	public LocalDateTime getLastModifiedDate() {
		return lastModifiedDate;
	}

	public void setLastModifiedDate(LocalDateTime lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((pid == null) ? 0 : pid.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PostDatedVoucher other = (PostDatedVoucher) obj;
		if (pid == null) {
			if (other.pid != null)
				return false;
		} else if (!pid.equals(other.pid))
			return false;
		return true;
	}
	
	
	
	
	
}

package com.orderfleet.webapp.domain;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
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

@Entity
@Table(name="tbl_post_dated_voucher_allocation")
public class PostDatedVoucherAllocation implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Id
	@GenericGenerator(name = "seq_post_dated_voucher_allocation_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "seq_post_dated_voucher_allocation_id") })
	@GeneratedValue(generator = "seq_post_dated_voucher_allocation_id_GEN")
	@Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_post_dated_voucher_allocation_id')")
	private Long id;

	@NotNull
	@Column(name = "pid", unique = true, nullable = false, updatable = false)
	private String pid;
	
	@Column(name = "alloc_Reference_Voucher")
	private String allocReferenceVoucher;
	
	@Column(name = "alloc_Reference_Voucher_Amount")
	private double allocReferenceVoucherAmount;
	
	@ManyToOne
	@JoinColumn(name = "post_dated_voucher_id")
	private PostDatedVoucher postDatedVoucher;
	
	@ManyToOne
	private Company company;

	@Column(name = "created_date", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	@JsonIgnore
	private LocalDateTime createdDate = LocalDateTime.now();

	@Column(name = "last_modified_date", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	@JsonIgnore
	private LocalDateTime lastModifiedDate = LocalDateTime.now();
	
	@Column(name = "voucher_number")
	private String voucherNumber;
	
	@PreUpdate
	public void preUpdate() {
		this.lastModifiedDate = LocalDateTime.now();
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

	public String getAllocReferenceVoucher() {
		return allocReferenceVoucher;
	}

	public void setAllocReferenceVoucher(String allocReferenceVoucher) {
		this.allocReferenceVoucher = allocReferenceVoucher;
	}

	public double getAllocReferenceVoucherAmount() {
		return allocReferenceVoucherAmount;
	}

	public void setAllocReferenceVoucherAmount(double allocReferenceVoucherAmount) {
		this.allocReferenceVoucherAmount = allocReferenceVoucherAmount;
	}

	public PostDatedVoucher getPostDatedVoucher() {
		return postDatedVoucher;
	}

	public void setPostDatedVoucher(PostDatedVoucher postDatedVoucher) {
		this.postDatedVoucher = postDatedVoucher;
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

	public String getVoucherNumber() {
		return voucherNumber;
	}

	public void setVoucherNumber(String voucherNumber) {
		this.voucherNumber = voucherNumber;
	}

	@Override
	public String toString() {
		return "PostDatedVoucherAllocation [allocReferenceVoucher=" + allocReferenceVoucher
				+ ", allocReferenceVoucherAmount=" + allocReferenceVoucherAmount + ", postDatedVoucher="
				+ postDatedVoucher + ", voucherNumber=" + voucherNumber + "]";
	}
	
}

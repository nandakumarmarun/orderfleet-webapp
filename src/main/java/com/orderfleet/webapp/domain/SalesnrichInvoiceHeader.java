package com.orderfleet.webapp.domain;

import java.io.Serializable;
import java.time.LocalDate;
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
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * A SalesnrichInvoiceHeader.
 * 
 * @author Sarath
 * @since Mar 15, 2018
 *
 */
@Entity
@Table(name = "tbl_salesnrich_invoice_header")
public class SalesnrichInvoiceHeader implements Serializable, Cloneable {

	private static final long serialVersionUID = 1L;

	@Id
	@GenericGenerator(name = "seq_salesnrich_invoice_header_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "seq_salesnrich_invoice_header_id") })
	@GeneratedValue(generator = "seq_salesnrich_invoice_header_id_GEN")
	@Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_salesnrich_invoice_header_id')")
	private Long id;

	@NotNull
	@Column(name = "invoice_number", unique = true, nullable = false)
	private Long invoiceNumber;

	@Column(name = "invoice_date", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	@JsonIgnore
	private LocalDate invoiceDate;

	@Column(name = "billing_from", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	@JsonIgnore
	private LocalDate billingFrom;

	@Column(name = "billing_to", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	@JsonIgnore
	private LocalDate billingTo;

	@NotNull
	@Column(name = "active_user_count", nullable = false)
	private Long activeUserCount;

	@NotNull
	@Column(name = "checked_user_count", nullable = false)
	private Long checkedUserCount;

	@NotNull
	@Column(name = "total_user_count", nullable = false)
	private Long totalUserCount;

	@Column(name = "gst_percentage", columnDefinition = "double precision DEFAULT 0")
	private double gstPercentage;

	@Column(name = "gst_amount", columnDefinition = "double precision DEFAULT 0")
	private double gstAmount;

	@Column(name = "sub_total", columnDefinition = "double precision DEFAULT 0")
	private double subTotal;

	@Column(name = "total_amount", columnDefinition = "double precision DEFAULT 0")
	private double totalAmount;

	@ManyToOne
	@NotNull
	private Company company;

	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name = "salesnrich_invoice_header_id")
	@Fetch(value = FetchMode.SUBSELECT)
	private List<SalesnrichInvoiceDetail> salesnrichInvoiceDetail;

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

	public SalesnrichInvoiceHeader() {
		super();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getInvoiceNumber() {
		return invoiceNumber;
	}

	public void setInvoiceNumber(Long invoiceNumber) {
		this.invoiceNumber = invoiceNumber;
	}

	public LocalDate getInvoiceDate() {
		return invoiceDate;
	}

	public void setInvoiceDate(LocalDate invoiceDate) {
		this.invoiceDate = invoiceDate;
	}

	public LocalDate getBillingFrom() {
		return billingFrom;
	}

	public void setBillingFrom(LocalDate billingFrom) {
		this.billingFrom = billingFrom;
	}

	public LocalDate getBillingTo() {
		return billingTo;
	}

	public void setBillingTo(LocalDate billingTo) {
		this.billingTo = billingTo;
	}

	public Long getActiveUserCount() {
		return activeUserCount;
	}

	public void setActiveUserCount(Long activeUserCount) {
		this.activeUserCount = activeUserCount;
	}

	public Long getCheckedUserCount() {
		return checkedUserCount;
	}

	public void setCheckedUserCount(Long checkedUserCount) {
		this.checkedUserCount = checkedUserCount;
	}

	public Long getTotalUserCount() {
		return totalUserCount;
	}

	public void setTotalUserCount(Long totalUserCount) {
		this.totalUserCount = totalUserCount;
	}

	public double getGstPercentage() {
		return gstPercentage;
	}

	public void setGstPercentage(double gstPercentage) {
		this.gstPercentage = gstPercentage;
	}

	public double getGstAmount() {
		return gstAmount;
	}

	public void setGstAmount(double gstAmount) {
		this.gstAmount = gstAmount;
	}

	public double getSubTotal() {
		return subTotal;
	}

	public void setSubTotal(double subTotal) {
		this.subTotal = subTotal;
	}

	public double getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(double totalAmount) {
		this.totalAmount = totalAmount;
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

	public LocalDateTime getLastModifiedDate() {
		return lastModifiedDate;
	}

	public List<SalesnrichInvoiceDetail> getSalesnrichInvoiceDetail() {
		return salesnrichInvoiceDetail;
	}

	public void setSalesnrichInvoiceDetail(List<SalesnrichInvoiceDetail> salesnrichInvoiceDetail) {
		this.salesnrichInvoiceDetail = salesnrichInvoiceDetail;
	}

}

package com.orderfleet.webapp.domain;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.orderfleet.webapp.domain.enums.SalesManagementStatus;
import com.orderfleet.webapp.domain.enums.TallyDownloadStatus;

/**
 * A AccountingVoucherHeader.
 * 
 * @author Muhammed Riyas T
 * @since July 27, 2016
 */
@Entity
@Table(name = "tbl_accounting_voucher_header")
public class AccountingVoucherHeader implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GenericGenerator(name = "seq_accounting_voucher_header_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "seq_accounting_voucher_header_id") })
	@GeneratedValue(generator = "seq_accounting_voucher_header_id_GEN")
	@Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_accounting_voucher_header_id')")
	private Long id;

	@NotNull
	@Column(name = "pid", unique = true, nullable = false, updatable = false)
	private String pid;

	@NotNull
	// @ManyToOne
	@ManyToOne
	private ExecutiveTaskExecution executiveTaskExecution;

	@NotNull
	@ManyToOne
	private Document document;

	@NotNull
	@ManyToOne
	private AccountProfile accountProfile;

	@NotNull
	@Column(name = "created_date", nullable = false)
	private LocalDateTime createdDate = LocalDateTime.now();

	@NotNull
	@Column(name = "document_date", nullable = false)
	private LocalDateTime documentDate;

	@Column(name = "total_amount", nullable = false)
	private double totalAmount;

	@Column(name = "outstanding_amount", nullable = false)
	private double outstandingAmount;

	@Column(name = "remarks", length = 1000)
	private String remarks;

	@NotNull
	@ManyToOne
	private User createdBy;

	@ManyToOne
	private EmployeeProfile employee;

	@ManyToOne
	@NotNull
	private Company company;

	// @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "accounting_voucher_header_id")
	@Fetch(value = FetchMode.SUBSELECT)
	private List<AccountingVoucherDetail> accountingVoucherDetails;

	@NotNull
	@Column(name = "document_number_local", unique = true, nullable = false, updatable = false)
	private String documentNumberLocal;

	@NotNull
	@Column(name = "document_number_server", unique = true, nullable = false, updatable = false)
	private String documentNumberServer;

	@NotNull
	@Column(name = "status", nullable = false)
	private Boolean status = false;

	@Column(name = "updated_date")
	private LocalDateTime updatedDate;

	@ManyToOne
	private User updatedBy;

	// status for tally download
	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "tally_download_status", nullable = false, columnDefinition = "character varying DEFAULT 'PENDING'")
	private TallyDownloadStatus tallyDownloadStatus = TallyDownloadStatus.PENDING;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "sales_management_status", nullable = false, columnDefinition = "character varying DEFAULT 'DEFAULT'")
	private SalesManagementStatus salesManagementStatus = SalesManagementStatus.DEFAULT;

	@Column(name = "imageRefNo")
	private String imageRefNo;

	@Column(name = "erp_ref_no")
	private String erpRefNo;

	@Column(name = "erp_status")
	private String erpStatus;

	@JsonIgnore
	@ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinTable(name = "tbl_receipt_file", joinColumns = {
			@JoinColumn(name = "receipt_id", referencedColumnName = "id") }, inverseJoinColumns = {
					@JoinColumn(name = "file_id", referencedColumnName = "id") })
	private Set<File> files = new HashSet<>();

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

	public ExecutiveTaskExecution getExecutiveTaskExecution() {
		return executiveTaskExecution;
	}

	public void setExecutiveTaskExecution(ExecutiveTaskExecution executiveTaskExecution) {
		this.executiveTaskExecution = executiveTaskExecution;
	}

	public Document getDocument() {
		return document;
	}

	public void setDocument(Document document) {
		this.document = document;
	}

	public AccountProfile getAccountProfile() {
		return accountProfile;
	}

	public void setAccountProfile(AccountProfile accountProfile) {
		this.accountProfile = accountProfile;
	}

	public LocalDateTime getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(LocalDateTime createdDate) {
		this.createdDate = createdDate;
	}

	public LocalDateTime getDocumentDate() {
		return documentDate;
	}

	public void setDocumentDate(LocalDateTime documentDate) {
		this.documentDate = documentDate;
	}

	public double getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(double totalAmount) {
		this.totalAmount = totalAmount;
	}

	public double getOutstandingAmount() {
		return outstandingAmount;
	}

	public void setOutstandingAmount(double outstandingAmount) {
		this.outstandingAmount = outstandingAmount;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public User getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(User createdBy) {
		this.createdBy = createdBy;
	}

	public EmployeeProfile getEmployee() {
		return employee;
	}

	public void setEmployee(EmployeeProfile employee) {
		this.employee = employee;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public List<AccountingVoucherDetail> getAccountingVoucherDetails() {
		return accountingVoucherDetails;
	}

	public void setAccountingVoucherDetails(List<AccountingVoucherDetail> accountingVoucherDetails) {
		this.accountingVoucherDetails = accountingVoucherDetails;
	}

	public String getDocumentNumberLocal() {
		return documentNumberLocal;
	}

	public void setDocumentNumberLocal(String documentNumberLocal) {
		this.documentNumberLocal = documentNumberLocal;
	}

	public String getDocumentNumberServer() {
		return documentNumberServer;
	}

	public void setDocumentNumberServer(String documentNumberServer) {
		this.documentNumberServer = documentNumberServer;
	}

	public Boolean getStatus() {
		return status;
	}

	public void setStatus(Boolean status) {
		this.status = status;
	}

	public LocalDateTime getUpdatedDate() {
		return updatedDate;
	}

	public void setUpdatedDate(LocalDateTime updatedDate) {
		this.updatedDate = updatedDate;
	}

	public User getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(User updatedBy) {
		this.updatedBy = updatedBy;
	}

	public TallyDownloadStatus getTallyDownloadStatus() {
		return tallyDownloadStatus;
	}

	public void setTallyDownloadStatus(TallyDownloadStatus tallyDownloadStatus) {
		this.tallyDownloadStatus = tallyDownloadStatus;
	}

	public SalesManagementStatus getSalesManagementStatus() {
		return salesManagementStatus;
	}

	public void setSalesManagementStatus(SalesManagementStatus salesManagementStatus) {
		this.salesManagementStatus = salesManagementStatus;
	}

	public String getImageRefNo() {
		return imageRefNo;
	}

	public void setImageRefNo(String imageRefNo) {
		this.imageRefNo = imageRefNo;
	}

	public Set<File> getFiles() {
		return files;
	}

	public void setFiles(Set<File> files) {
		this.files = files;
	}

	public String getErpRefNo() {
		return erpRefNo;
	}

	public void setErpRefNo(String erpRefNo) {
		this.erpRefNo = erpRefNo;
	}

	public String getErpStatus() {
		return erpStatus;
	}

	public void setErpStatus(String erpStatus) {
		this.erpStatus = erpStatus;
	}

}

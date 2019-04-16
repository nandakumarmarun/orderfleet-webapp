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
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

/**
 * A AccountingVoucherHeaderHistory.
 * 
 * @author Muhammed Riyas T
 * @since October 24, 2016
 */
@Entity
@Table(name = "tbl_accounting_voucher_header_history")
public class AccountingVoucherHeaderHistory implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GenericGenerator(name = "seq_accounting_voucher_header_history_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "seq_accounting_voucher_header_history_id") })
	@GeneratedValue(generator = "seq_accounting_voucher_header_history_id_GEN")
	@Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_accounting_voucher_header_history_id')")
	private Long id;

	@NotNull
	@Column(name = "pid", nullable = false, updatable = false)
	private String pid;

	@NotNull
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

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "accounting_voucher_header_history_id")
	private List<AccountingVoucherDetailHistory> accountingVoucherDetails;

	@NotNull
	@Column(name = "document_number_local", nullable = false, updatable = false)
	private String documentNumberLocal;

	@NotNull
	@Column(name = "document_number_server", nullable = false, updatable = false)
	private String documentNumberServer;

	public AccountingVoucherHeaderHistory() {

	}

	public AccountingVoucherHeaderHistory(AccountingVoucherHeader accountingVoucherHeader) {
		super();
		this.pid = accountingVoucherHeader.getPid();
		this.executiveTaskExecution = accountingVoucherHeader.getExecutiveTaskExecution();
		this.document = accountingVoucherHeader.getDocument();
		this.accountProfile = accountingVoucherHeader.getAccountProfile();
		this.createdDate = accountingVoucherHeader.getCreatedDate();
		this.documentDate = accountingVoucherHeader.getDocumentDate();
		this.totalAmount = accountingVoucherHeader.getTotalAmount();
		this.outstandingAmount = accountingVoucherHeader.getOutstandingAmount();
		this.remarks = accountingVoucherHeader.getRemarks();
		this.createdBy = accountingVoucherHeader.getCreatedBy();
		this.employee = accountingVoucherHeader.getEmployee();
		this.company = accountingVoucherHeader.getCompany();
		this.documentNumberLocal = accountingVoucherHeader.getDocumentNumberLocal();
		this.documentNumberServer = accountingVoucherHeader.getDocumentNumberServer();
		this.accountingVoucherDetails = accountingVoucherHeader.getAccountingVoucherDetails().stream()
				.map(AccountingVoucherDetailHistory::new).collect(Collectors.toList());
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

	public List<AccountingVoucherDetailHistory> getAccountingVoucherDetails() {
		return accountingVoucherDetails;
	}

	public void setAccountingVoucherDetails(List<AccountingVoucherDetailHistory> accountingVoucherDetails) {
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

}

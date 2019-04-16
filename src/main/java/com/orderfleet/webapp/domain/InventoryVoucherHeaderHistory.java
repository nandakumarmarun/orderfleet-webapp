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
 * A InventoryVoucherHeaderHistory.
 * 
 * @author Muhammed Riyas T
 * @since October 24, 2016
 */
@Entity
@Table(name = "tbl_inventory_voucher_header_history")
public class InventoryVoucherHeaderHistory implements Serializable {

	private static final long serialVersionUID = -166802392162260209L;

	@Id
	@GenericGenerator(name = "seq_inventory_voucher_header_history_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "seq_inventory_voucher_header_history_id") })
	@GeneratedValue(generator = "seq_inventory_voucher_header_history_id_GEN")
	@Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_inventory_voucher_header_history_id')")
	private Long id;

	@NotNull
	@Column(name = "pid", nullable = false, updatable = false)
	private String pid;

	@NotNull
	@Column(name = "document_number_local", nullable = false, updatable = false)
	private String documentNumberLocal;

	@NotNull
	@Column(name = "document_number_server", nullable = false, updatable = false)
	private String documentNumberServer;

	@NotNull
	@Column(name = "created_date", nullable = false)
	private LocalDateTime createdDate = LocalDateTime.now();

	@NotNull
	@Column(name = "document_date", nullable = false)
	private LocalDateTime documentDate;

	@NotNull
	@ManyToOne
	private Document document;

	@NotNull
	@ManyToOne
	private AccountProfile receiverAccount;

	@ManyToOne
	private AccountProfile supplierAccount;

	@NotNull
	@ManyToOne
	private User createdBy;

	@ManyToOne
	private EmployeeProfile employee;

	@Column(name = "document_total", nullable = false)
	private double documentTotal;

	@Column(name = "document_volume", nullable = false, columnDefinition = "double precision DEFAULT 0")
	private double documentVolume;

	@Column(name = "doc_discount_amount", nullable = false, columnDefinition = "double precision DEFAULT 0 ")
	private double docDiscountAmount;

	@Column(name = "doc_discount_percentage", nullable = false, columnDefinition = "double precision DEFAULT 0 ")
	private double docDiscountPercentage;

	@NotNull
	@ManyToOne
	private ExecutiveTaskExecution executiveTaskExecution;

	@ManyToOne
	@NotNull
	private Company company;

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "inventory_voucher_header_history_id")
	private List<InventoryVoucherDetailHistory> inventoryVoucherDetails;

	@NotNull
	@Column(name = "status", nullable = false, columnDefinition = "boolean DEFAULT 'FALSE' ")
	private Boolean status = false;

	@ManyToOne
	@JoinColumn(name = "price_level_id")
	private PriceLevel priceLevel;

	public InventoryVoucherHeaderHistory() {

	}

	public InventoryVoucherHeaderHistory(InventoryVoucherHeader inventoryVoucherHeader) {
		super();
		this.pid = inventoryVoucherHeader.getPid();
		this.documentNumberLocal = inventoryVoucherHeader.getDocumentNumberLocal();
		this.documentNumberServer = inventoryVoucherHeader.getDocumentNumberServer();
		this.createdDate = inventoryVoucherHeader.getCreatedDate();
		this.documentDate = inventoryVoucherHeader.getDocumentDate();
		this.document = inventoryVoucherHeader.getDocument();
		this.receiverAccount = inventoryVoucherHeader.getReceiverAccount();
		this.supplierAccount = inventoryVoucherHeader.getSupplierAccount();
		this.createdBy = inventoryVoucherHeader.getCreatedBy();
		this.employee = inventoryVoucherHeader.getEmployee();
		this.documentTotal = inventoryVoucherHeader.getDocumentTotal();
		this.documentVolume = inventoryVoucherHeader.getDocumentVolume();
		this.executiveTaskExecution = inventoryVoucherHeader.getExecutiveTaskExecution();
		this.company = inventoryVoucherHeader.getCompany();
		this.inventoryVoucherDetails = inventoryVoucherHeader.getInventoryVoucherDetails().stream()
				.map(InventoryVoucherDetailHistory::new).collect(Collectors.toList());
		this.status = inventoryVoucherHeader.getStatus();
		this.priceLevel = inventoryVoucherHeader.getPriceLevel();
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

	public Document getDocument() {
		return document;
	}

	public void setDocument(Document document) {
		this.document = document;
	}

	public AccountProfile getReceiverAccount() {
		return receiverAccount;
	}

	public void setReceiverAccount(AccountProfile receiverAccount) {
		this.receiverAccount = receiverAccount;
	}

	public AccountProfile getSupplierAccount() {
		return supplierAccount;
	}

	public void setSupplierAccount(AccountProfile supplierAccount) {
		this.supplierAccount = supplierAccount;
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

	public double getDocumentTotal() {
		return documentTotal;
	}

	public void setDocumentTotal(double documentTotal) {
		this.documentTotal = documentTotal;
	}

	public double getDocumentVolume() {
		return documentVolume;
	}

	public void setDocumentVolume(double documentVolume) {
		this.documentVolume = documentVolume;
	}

	public double getDocDiscountAmount() {
		return docDiscountAmount;
	}

	public void setDocDiscountAmount(double docDiscountAmount) {
		this.docDiscountAmount = docDiscountAmount;
	}

	public double getDocDiscountPercentage() {
		return docDiscountPercentage;
	}

	public void setDocDiscountPercentage(double docDiscountPercentage) {
		this.docDiscountPercentage = docDiscountPercentage;
	}

	public ExecutiveTaskExecution getExecutiveTaskExecution() {
		return executiveTaskExecution;
	}

	public void setExecutiveTaskExecution(ExecutiveTaskExecution executiveTaskExecution) {
		this.executiveTaskExecution = executiveTaskExecution;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public List<InventoryVoucherDetailHistory> getInventoryVoucherDetails() {
		return inventoryVoucherDetails;
	}

	public void setInventoryVoucherDetails(List<InventoryVoucherDetailHistory> inventoryVoucherDetails) {
		this.inventoryVoucherDetails = inventoryVoucherDetails;
	}

	public Boolean getStatus() {
		return status;
	}

	public void setStatus(Boolean status) {
		this.status = status;
	}

	public PriceLevel getPriceLevel() {
		return priceLevel;
	}

	public void setPriceLevel(PriceLevel priceLevel) {
		this.priceLevel = priceLevel;
	}
}

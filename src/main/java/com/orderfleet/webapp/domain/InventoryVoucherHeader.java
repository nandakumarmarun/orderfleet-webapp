package com.orderfleet.webapp.domain;

import java.io.Serializable;
import java.time.LocalDate;
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
import com.orderfleet.webapp.domain.enums.ProcessFlowStatus;
import com.orderfleet.webapp.domain.enums.SalesManagementStatus;
import com.orderfleet.webapp.domain.enums.SalesOrderStatus;
import com.orderfleet.webapp.domain.enums.SendSalesOrderEmailStatus;
import com.orderfleet.webapp.domain.enums.SourceModule;
import com.orderfleet.webapp.domain.enums.TallyDownloadStatus;

/**
 * A InventoryVoucherHeader.
 * 
 * @author Muhammed Riyas T
 * @since July 18, 2016
 */
@Entity
@Table(name = "tbl_inventory_voucher_header")
public class InventoryVoucherHeader implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GenericGenerator(name = "seq_inventory_voucher_header_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "seq_inventory_voucher_header_id") })
	@GeneratedValue(generator = "seq_inventory_voucher_header_id_GEN")
	@Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_inventory_voucher_header_id')")
	private Long id;

	@NotNull
	@Column(name = "pid", unique = true, nullable = false, updatable = false)
	private String pid;

	@NotNull
	@Column(name = "document_number_local", unique = true, nullable = false, updatable = false)
	private String documentNumberLocal;

	@NotNull
	@Column(name = "document_number_server", unique = true, nullable = false, updatable = false)
	private String documentNumberServer;

	// custom status that customer can change - Pending/Processed etc..
	@NotNull
	@Column(name = "process_status", nullable = false, columnDefinition = "varchar(100) DEFAULT 'Pending'")
	private String processStatus = "Pending";

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

	@Column(name = "document_total_updated", nullable = false, columnDefinition = "double precision DEFAULT 0")
	private double documentTotalUpdated;

	@Column(name = "document_volume", nullable = false, columnDefinition = "double precision DEFAULT 0")
	private double documentVolume;

	@Column(name = "document_volume_updated", nullable = false, columnDefinition = "double precision DEFAULT 0")
	private double documentVolumeUpdated;

	@Column(name = "doc_discount_amount", nullable = false, columnDefinition = "double precision DEFAULT 0 ")
	private double docDiscountAmount;

	@Column(name = "doc_discount_percentage", nullable = false, columnDefinition = "double precision DEFAULT 0 ")
	private double docDiscountPercentage;

	@NotNull
	// @ManyToOne
	@ManyToOne
	private ExecutiveTaskExecution executiveTaskExecution;

	@ManyToOne
	@NotNull
	private Company company;

//	 @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "inventory_voucher_header_id")
	@Fetch(value = FetchMode.SUBSELECT)
	private List<InventoryVoucherDetail> inventoryVoucherDetails;

	// tally download status
	@NotNull
	@Column(name = "status", nullable = false)
	private Boolean status = false;

	@ManyToOne
	@JoinColumn(name = "price_level_id")
	private PriceLevel priceLevel;

	@Enumerated(EnumType.STRING)
	@Column(name = "source_module")
	private SourceModule sourceModule;

	@Column(name = "reference_document_number", updatable = false)
	private String referenceDocumentNumber;

	@Column(name = "order_number") // for increment order number
	private Long orderNumber;

	@Column(name = "reference_document_type", updatable = false)
	private String referenceDocumentType;

	// custom status for a company
	@ManyToOne
	private OrderStatus orderStatus;

	@Column(name = "updated_date")
	private LocalDateTime updatedDate;

	@Column(name = "booking_id")
	private String bookingId;

	@Column(name = "delivery_date")
	private LocalDate deliveryDate;

	@Column(name = "booking_date")
	private LocalDate bookingDate;

	@Column(name = "validation_days")
	private String validationDays;

	@ManyToOne
	private User updatedBy;

	@ManyToOne
	private SalesLedger salesLedger;

	// status for tally download
	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "tally_download_status", nullable = false, columnDefinition = "character varying DEFAULT 'PENDING'")
	private TallyDownloadStatus tallyDownloadStatus = TallyDownloadStatus.PENDING;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "sales_management_status", nullable = false, columnDefinition = "character varying DEFAULT 'DEFAULT'")
	private SalesManagementStatus salesManagementStatus = SalesManagementStatus.DEFAULT;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "sales_order_status", nullable = false, columnDefinition = "character varying DEFAULT 'CREATED'")
	private SalesOrderStatus  salesOrderStatus = SalesOrderStatus.CREATED;
	
	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "process_flow_status", nullable = false, columnDefinition = "character varying DEFAULT 'DEFAULT'")
	private ProcessFlowStatus processFlowStatus = ProcessFlowStatus.DEFAULT;

	@NotNull
	@Column(name = "pdf_download_status", nullable = false, columnDefinition = "boolean DEFAULT 'FALSE'")
	private boolean pdfDownloadStatus = false;

	@NotNull
	@Column(name = "updated_status", nullable = false, columnDefinition = "boolean DEFAULT 'FALSE'")
	private boolean updatedStatus = false;// whether the inventory voucher is updated or not

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "send_sales_order_status", nullable = false, columnDefinition = "character varying DEFAULT 'NOT_SENT'")
	private SendSalesOrderEmailStatus sendSalesOrderEmailStatus = SendSalesOrderEmailStatus.NOT_SENT;

	@Column(name = "payment_received", nullable = false, columnDefinition = "double precision DEFAULT 0 ")
	private double paymentReceived;

	@Column(name = "reference_invoice_number")
	private String referenceInvoiceNumber;

	@Column(name = "rounded_off", nullable = false, columnDefinition = "double precision DEFAULT 0")
	private double roundedOff;

	@Column(name = "erp_reference_number")
	private String erpReferenceNumber;

	@Column(name = "erp_status")
	private String erpStatus;

	@NotNull
	@Column(name = "rejected_status", nullable = false, columnDefinition = "boolean DEFAULT 'FALSE'")
	private boolean rejectedStatus = false;// whether the inventory voucher is rejected or not

	@Column(name = "remarks")
	private String remarks;

	@Column(name = "imageRefNo")
	private String imageRefNo;

	@Column(name = "count",  columnDefinition = "double precision DEFAULT 0")
	private Double count;

	@JsonIgnore
	@ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinTable(name = "tbl_invetory_voucher_file", joinColumns = {
			@JoinColumn(name = "inventory_voucher_id", referencedColumnName = "id") }, inverseJoinColumns = {
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

	public SalesManagementStatus getSalesManagementStatus() {
		return salesManagementStatus;
	}

	public void setSalesManagementStatus(SalesManagementStatus salesManagementStatus) {
		this.salesManagementStatus = salesManagementStatus;
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

	public double getDocumentTotalUpdated() {
		return documentTotalUpdated;
	}

	public void setDocumentTotalUpdated(double documentTotalUpdated) {
		this.documentTotalUpdated = documentTotalUpdated;
	}

	public double getDocumentVolumeUpdated() {
		return documentVolumeUpdated;
	}

	public void setDocumentVolumeUpdated(double documentVolumeUpdated) {
		this.documentVolumeUpdated = documentVolumeUpdated;
	}

	public double getPaymentReceived() {
		return paymentReceived;
	}

	public void setPaymentReceived(double paymentReceived) {
		this.paymentReceived = paymentReceived;
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

	public List<InventoryVoucherDetail> getInventoryVoucherDetails() {
		return inventoryVoucherDetails;
	}

	public void setInventoryVoucherDetails(List<InventoryVoucherDetail> inventoryVoucherDetails) {
		this.inventoryVoucherDetails = inventoryVoucherDetails;
	}

	public Boolean getStatus() {
		return status;
	}

	public void setStatus(Boolean status) {
		this.status = status;
	}

	public boolean getPdfDownloadStatus() {
		return pdfDownloadStatus;
	}

	public void setPdfDownloadStatus(boolean pdfDownloadStatus) {
		this.pdfDownloadStatus = pdfDownloadStatus;
	}

	public PriceLevel getPriceLevel() {
		return priceLevel;
	}

	public void setPriceLevel(PriceLevel priceLevel) {
		this.priceLevel = priceLevel;
	}

	public SourceModule getSourceModule() {
		return sourceModule;
	}

	public void setSourceModule(SourceModule sourceModule) {
		this.sourceModule = sourceModule;
	}

	public String getReferenceDocumentNumber() {
		return referenceDocumentNumber;
	}

	public void setReferenceDocumentNumber(String referenceDocumentNumber) {
		this.referenceDocumentNumber = referenceDocumentNumber;
	}

	public Long getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(Long orderNumber) {
		this.orderNumber = orderNumber;
	}

	public String getReferenceDocumentType() {
		return referenceDocumentType;
	}

	public void setReferenceDocumentType(String referenceDocumentType) {
		this.referenceDocumentType = referenceDocumentType;
	}

	public String getProcessStatus() {
		return processStatus;
	}

	public void setProcessStatus(String processStatus) {
		this.processStatus = processStatus;
	}

	public OrderStatus getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(OrderStatus orderStatus) {
		this.orderStatus = orderStatus;
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

	public boolean getUpdatedStatus() {
		return updatedStatus;
	}

	public void setUpdatedStatus(boolean updatedStatus) {
		this.updatedStatus = updatedStatus;
	}

	public SendSalesOrderEmailStatus getSendSalesOrderEmailStatus() {
		return sendSalesOrderEmailStatus;
	}

	public void setSendSalesOrderEmailStatus(SendSalesOrderEmailStatus sendSalesOrderEmailStatus) {
		this.sendSalesOrderEmailStatus = sendSalesOrderEmailStatus;
	}

	public ProcessFlowStatus getProcessFlowStatus() {
		return processFlowStatus;
	}

	public void setProcessFlowStatus(ProcessFlowStatus processFlowStatus) {
		this.processFlowStatus = processFlowStatus;
	}

	public String getBookingId() {
		return bookingId;
	}

	public void setBookingId(String bookingId) {
		this.bookingId = bookingId;
	}

	public LocalDate getDeliveryDate() {
		return deliveryDate;
	}

	public void setDeliveryDate(LocalDate deliveryDate) {
		this.deliveryDate = deliveryDate;
	}

	public String getReferenceInvoiceNumber() {
		return referenceInvoiceNumber;
	}

	public void setReferenceInvoiceNumber(String referenceInvoiceNumber) {
		this.referenceInvoiceNumber = referenceInvoiceNumber;
	}

	public double getRoundedOff() {
		return roundedOff;
	}

	public void setRoundedOff(double roundedOff) {
		this.roundedOff = roundedOff;
	}

	public String getErpReferenceNumber() {
		return erpReferenceNumber;
	}

	public void setErpReferenceNumber(String erpReferenceNumber) {
		this.erpReferenceNumber = erpReferenceNumber;
	}

	public String getErpStatus() {
		return erpStatus;
	}

	public void setErpStatus(String erpStatus) {
		this.erpStatus = erpStatus;
	}

	public boolean getRejectedStatus() {
		return rejectedStatus;
	}

	public void setRejectedStatus(boolean rejectedStatus) {
		this.rejectedStatus = rejectedStatus;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public LocalDate getBookingDate() {
		return bookingDate;
	}

	public void setBookingDate(LocalDate bookingDate) {
		this.bookingDate = bookingDate;
	}

	public String getValidationDays() {
		return validationDays;
	}

	public void setValidationDays(String validationDays) {
		this.validationDays = validationDays;
	}

	public SalesLedger getSalesLedger() {
		return salesLedger;
	}

	public void setSalesLedger(SalesLedger salesLedger) {
		this.salesLedger = salesLedger;
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

	public SalesOrderStatus getSalesOrderStatus() {
		return salesOrderStatus;
	}

	public void setSalesOrderStatus(SalesOrderStatus salesOrderStatus) {
		this.salesOrderStatus = salesOrderStatus;
	}

	public Double getCount() {
		return count;
	}

	public void setCount(Double count) {
		this.count = count;
	}
}

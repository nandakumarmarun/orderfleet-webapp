package com.orderfleet.webapp.domain;

import com.orderfleet.webapp.domain.enums.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_invoice_details_denormalized")
public class InvoiceDetailsDenormalized {
    private static final long serialVersionUID = 1L;

    @Id
    @GenericGenerator(name = "seq_invoice_details_denormalized_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
            @org.hibernate.annotations.Parameter(name = "sequence_name", value = "seq_invoice_details_denormalized_id") })
    @GeneratedValue(generator = "seq_invoice_details_denormalized_id_GEN")
    @Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_invoice_details_denormalized_id')")
    private Long id;

    @NotNull
    @Column(name = "pid", unique = true, nullable = false, updatable = false)
    private String pid;

    @NotNull
    @Column(name = "executive_task_execution_pid",  nullable = false, updatable = false)
    private String executionPid;

    @NotNull
    private String userPid;
    @NotNull
    private String userName;

    private String employeePid;

    private LocationType locationType;
    private String towerLocation;
    private  Double salesOrderTotalAmount;
    private Double receiptAmount;
    private String vehicleNumber;
    private String remarks;

    @NotNull
    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdDate = LocalDateTime.now();

    @NotNull
    @Column(name = "date", nullable = false)
    private LocalDateTime date;

    @Column(name = "send_date")
    private LocalDateTime sendDate;
    @NotNull
    private String activityName;
    @NotNull
    private String activityPid;
    @NotNull
    private String accountTypeName;
    @NotNull
    private String accountTypePid;


    @NotNull
    private String accountProfileName;
    @NotNull
    private String accountProfilePid;
    private String accountPhNo;
    @NotNull
    private String companyName;
    @NotNull
    private String companyPid;
    @Column(name = "punch_in_date")
    private LocalDateTime punchInDate;

    private String location;
    @NotNull
    @Column(name = "document_number_local", nullable = false, updatable = false)
    private String documentNumberLocal;

    @NotNull
    @Column(name = "document_number_server", nullable = false, updatable = false)
    private String documentNumberServer;


    @Column(name = "process_status", nullable = false, columnDefinition = "varchar(100) DEFAULT 'Pending'")
    private String processStatus = "Pending";
    @NotNull
    @Column(name = "document_date", nullable = false)
    private LocalDateTime documentDate;

    @NotNull
    private String documentName;
    @NotNull
    private String documentPid;

    private String receiverAccountName;


    private String supplierAccountName;

    @NotNull
    private String createdBy;

    private String employeeName;

    @Column(name = "document_total", nullable = false)
    private double documentTotal;

    @Column(name = "document_volume", nullable = false, columnDefinition = "double precision DEFAULT 0")
    private double documentVolume;

    @Column(name = "doc_discount_amount", nullable = false, columnDefinition = "double precision DEFAULT 0 ")
    private double docDiscountAmount;

    @Column(name = "doc_discount_percentage", nullable = false, columnDefinition = "double precision DEFAULT 0 ")
    private double docDiscountPercentage;
    @Column(name = "updated_date")
    private LocalDateTime updatedDate;
    private String productName;
    private String productRemarks;

    @Column(name = "quantity")
    private double quantity;
    @Column(name = "free_quantity")
    private double freeQuantity;

    @Column(name = "selling_rate")
    private double sellingRate;
    @Column(name = "tax_percentage")
    private double taxPercentage;

    @Column(name = "discount_percentage")
    private double discountPercentage;
    @Column(name = "discount_amount")
    private double discountAmount;

    private double totalAmount;

    @Column(name = "outstanding_amount", nullable = false)
    private double outstandingAmount;
    @Column(name = "mode")
    private PaymentMode mode;

    @Column(name = "amount")
    private double amount;

    @Column(name = "instrument_number")
    private String instrumentNumber;

    @Column(name = "instrument_date")
    private LocalDateTime instrumentDate;

    @Column(name = "bank_name")
    private String bankName;
    @Column(name = "bank_pid")
    private String bankPid;


    private String byAccountName;
    private String byAccountPid;

    private String toAccountName;
    private String toAccountPid;
    @Column(name = "voucher_number")
    private String voucherNumber;

    @Column(name = "voucher_date")
    private LocalDateTime voucherDate;

    @Column(name = "reference_number")
    private String referenceNumber;

    private String provisionalReceiptNo;
    @ManyToOne
    private IncomeExpenseHead incomeExpenseHead;


    private String formName;

    //@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @Column(name = "value")
    private String value;

    //@ManyToOne

    private String formElementName;

    @Column(name = "mock_location_status", columnDefinition = "boolean DEFAULT 'FALSE'")
    private boolean mockLocationStatus;

    private String receiptRemarks;
    @Enumerated(EnumType.STRING)
    @Column(name = "document_type", nullable = false)
    private DocumentType documentType;
    private boolean withCustomer;
    @NotNull
    private Long companyId;

    private String formElementPid;
    private String formElementType;

    private Long userId;

    private String inventoryPid;
    private String accountingPid;
    private String dynamicPid;

    public InvoiceDetailsDenormalized() {
    }

    public InvoiceDetailsDenormalized(Long id, String pid, String executionPid, String userPid, String userName, String employeePid, LocationType locationType, String towerLocation, Double salesOrderTotalAmount, Double receiptAmount, String vehicleNumber, String remarks, LocalDateTime createdDate, LocalDateTime date, LocalDateTime sendDate, String activityName, String accountTypeName, String accountTypePid, String accountProfileName, String accountProfilePid, String accountPhNo, String companyName, String companyPid, LocalDateTime punchInDate, String location, String documentNumberLocal, String processStatus, LocalDateTime documentDate, String documentName, String receiverAccountName, String supplierAccountName, String createdBy, String employeeName, double documentTotal, double documentVolume, double docDiscountAmount, double docDiscountPercentage, LocalDateTime updatedDate, String productName, String productRemarks, double quantity, double freeQuantity, double sellingRate, double taxPercentage, double discountPercentage, double discountAmount, double totalAmount, double outstandingAmount, PaymentMode mode, double amount,
                                     Long userId, String instrumentNumber, LocalDateTime instrumentDate, String bankName, String bankPid, String byAccountName, String byAccountPid, String toAccountName, String toAccountPid, String voucherNumber, LocalDateTime voucherDate, String referenceNumber, String provisionalReceiptNo, IncomeExpenseHead incomeExpenseHead, String formName, String value, String formElement,String formElementPid, boolean mockLocationStatus, String receiptRemarks, DocumentType documentType,String activityPid,String documentPid,boolean withCustomer) {
        this.id = id;
        this.pid = pid;
        this.executionPid = executionPid;
        this.userPid = userPid;
        this.userName = userName;
        this.employeePid = employeePid;
        this.locationType = locationType;
        this.towerLocation = towerLocation;
        this.salesOrderTotalAmount = salesOrderTotalAmount;
        this.receiptAmount = receiptAmount;
        this.vehicleNumber = vehicleNumber;
        this.remarks = remarks;
        this.createdDate = createdDate;
        this.date = date;
        this.sendDate = sendDate;
        this.activityName = activityName;
        this.accountTypeName = accountTypeName;
        this.accountTypePid = accountTypePid;
        this.accountProfileName = accountProfileName;
        this.accountProfilePid = accountProfilePid;
        this.accountPhNo = accountPhNo;
        this.companyName = companyName;
        this.companyPid = companyPid;
        this.punchInDate = punchInDate;
        this.location = location;
        this.documentNumberLocal = documentNumberLocal;
        this.processStatus = processStatus;
        this.documentDate = documentDate;
        this.documentName = documentName;
        this.receiverAccountName = receiverAccountName;
        this.supplierAccountName = supplierAccountName;
        this.createdBy = createdBy;
        this.employeeName = employeeName;
        this.documentTotal = documentTotal;
        this.documentVolume = documentVolume;
        this.docDiscountAmount = docDiscountAmount;
        this.docDiscountPercentage = docDiscountPercentage;
        this.updatedDate = updatedDate;
        this.productName = productName;
        this.productRemarks = productRemarks;
        this.quantity = quantity;
        this.freeQuantity = freeQuantity;
        this.sellingRate = sellingRate;
        this.taxPercentage = taxPercentage;
        this.discountPercentage = discountPercentage;
        this.discountAmount = discountAmount;
        this.totalAmount = totalAmount;
        this.outstandingAmount = outstandingAmount;
        this.mode = mode;
        this.amount = amount;
        this.instrumentNumber = instrumentNumber;
        this.instrumentDate = instrumentDate;
        this.bankName = bankName;
        this.bankPid = bankPid;
        this.byAccountName = byAccountName;
        this.byAccountPid = byAccountPid;
        this.toAccountName = toAccountName;
        this.toAccountPid = toAccountPid;
        this.voucherNumber = voucherNumber;
        this.voucherDate = voucherDate;
        this.referenceNumber = referenceNumber;
        this.provisionalReceiptNo = provisionalReceiptNo;
        this.incomeExpenseHead = incomeExpenseHead;
        this.formName = formName;
        this.value = value;
        this.formElementName = formElement;
        this.mockLocationStatus = mockLocationStatus;
        this.receiptRemarks = receiptRemarks;
        this.documentType = documentType;
        this.withCustomer = withCustomer;
        this.userId = userId;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
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

    public String getExecutionPid() {
        return executionPid;
    }

    public void setExecutionPid(String executionPid) {
        this.executionPid = executionPid;
    }

    public String getUserPid() {
        return userPid;
    }

    public void setUserPid(String userPid) {
        this.userPid = userPid;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmployeePid() {
        return employeePid;
    }

    public void setEmployeePid(String employeePid) {
        this.employeePid = employeePid;
    }

    public LocationType getLocationType() {
        return locationType;
    }

    public void setLocationType(LocationType locationType) {
        this.locationType = locationType;
    }

    public String getTowerLocation() {
        return towerLocation;
    }

    public void setTowerLocation(String towerLocation) {
        this.towerLocation = towerLocation;
    }

    public Double getSalesOrderTotalAmount() {
        return salesOrderTotalAmount;
    }

    public void setSalesOrderTotalAmount(Double salesOrderTotalAmount) {
        this.salesOrderTotalAmount = salesOrderTotalAmount;
    }

    public Double getReceiptAmount() {
        return receiptAmount;
    }

    public void setReceiptAmount(Double receiptAmount) {
        this.receiptAmount = receiptAmount;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        vehicleNumber = vehicleNumber;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public LocalDateTime getSendDate() {
        return sendDate;
    }

    public void setSendDate(LocalDateTime sendDate) {
        this.sendDate = sendDate;
    }

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public String getAccountTypeName() {
        return accountTypeName;
    }

    public void setAccountTypeName(String accountTypeName) {
        this.accountTypeName = accountTypeName;
    }

    public String getAccountTypePid() {
        return accountTypePid;
    }

    public void setAccountTypePid(String accountTypePid) {
        this.accountTypePid = accountTypePid;
    }

    public String getAccountProfileName() {
        return accountProfileName;
    }

    public void setAccountProfileName(String accountProfileName) {
        this.accountProfileName = accountProfileName;
    }

    public String getAccountProfilePid() {
        return accountProfilePid;
    }

    public void setAccountProfilePid(String accountProfilePid) {
        this.accountProfilePid = accountProfilePid;
    }

    public String getAccountPhNo() {
        return accountPhNo;
    }

    public void setAccountPhNo(String accountPhNo) {
        this.accountPhNo = accountPhNo;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyPid() {
        return companyPid;
    }

    public void setCompanyPid(String companyPid) {
        this.companyPid = companyPid;
    }

    public LocalDateTime getPunchInDate() {
        return punchInDate;
    }

    public void setPunchInDate(LocalDateTime punchInDate) {
        this.punchInDate = punchInDate;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDocumentNumberLocal() {
        return documentNumberLocal;
    }

    public void setDocumentNumberLocal(String documentNumberLocal) {
        this.documentNumberLocal = documentNumberLocal;
    }

    public String getProcessStatus() {
        return processStatus;
    }

    public void setProcessStatus(String processStatus) {
        this.processStatus = processStatus;
    }

    public LocalDateTime getDocumentDate() {
        return documentDate;
    }

    public void setDocumentDate(LocalDateTime documentDate) {
        this.documentDate = documentDate;
    }

    public String getDocumentName() {
        return documentName;
    }

    public void setDocumentName(String documentName) {
        this.documentName = documentName;
    }

    public String getReceiverAccountName() {
        return receiverAccountName;
    }

    public void setReceiverAccountName(String receiverAccountName) {
        this.receiverAccountName = receiverAccountName;
    }

    public String getSupplierAccountName() {
        return supplierAccountName;
    }

    public void setSupplierAccountName(String supplierAccountName) {
        this.supplierAccountName = supplierAccountName;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
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

    public LocalDateTime getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(LocalDateTime updatedDate) {
        this.updatedDate = updatedDate;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductRemarks() {
        return productRemarks;
    }

    public void setProductRemarks(String productRemarks) {
        this.productRemarks = productRemarks;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public double getFreeQuantity() {
        return freeQuantity;
    }

    public void setFreeQuantity(double freeQuantity) {
        this.freeQuantity = freeQuantity;
    }

    public double getSellingRate() {
        return sellingRate;
    }

    public void setSellingRate(double sellingRate) {
        this.sellingRate = sellingRate;
    }

    public double getTaxPercentage() {
        return taxPercentage;
    }

    public void setTaxPercentage(double taxPercentage) {
        this.taxPercentage = taxPercentage;
    }

    public double getDiscountPercentage() {
        return discountPercentage;
    }

    public void setDiscountPercentage(double discountPercentage) {
        this.discountPercentage = discountPercentage;
    }

    public double getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(double discountAmount) {
        this.discountAmount = discountAmount;
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

    public PaymentMode getMode() {
        return mode;
    }

    public void setMode(PaymentMode mode) {
        this.mode = mode;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getInstrumentNumber() {
        return instrumentNumber;
    }

    public void setInstrumentNumber(String instrumentNumber) {
        this.instrumentNumber = instrumentNumber;
    }

    public LocalDateTime getInstrumentDate() {
        return instrumentDate;
    }

    public void setInstrumentDate(LocalDateTime instrumentDate) {
        this.instrumentDate = instrumentDate;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getBankPid() {
        return bankPid;
    }

    public void setBankPid(String bankPid) {
        this.bankPid = bankPid;
    }

    public String getByAccountName() {
        return byAccountName;
    }

    public void setByAccountName(String byAccountName) {
        this.byAccountName = byAccountName;
    }

    public String getByAccountPid() {
        return byAccountPid;
    }

    public void setByAccountPid(String byAccountPid) {
        this.byAccountPid = byAccountPid;
    }

    public String getToAccountName() {
        return toAccountName;
    }

    public void setToAccountName(String toAccountName) {
        this.toAccountName = toAccountName;
    }

    public String getToAccountPid() {
        return toAccountPid;
    }

    public void setToAccountPid(String toAccountPid) {
        this.toAccountPid = toAccountPid;
    }

    public String getVoucherNumber() {
        return voucherNumber;
    }

    public void setVoucherNumber(String voucherNumber) {
        this.voucherNumber = voucherNumber;
    }

    public LocalDateTime getVoucherDate() {
        return voucherDate;
    }

    public void setVoucherDate(LocalDateTime voucherDate) {
        this.voucherDate = voucherDate;
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }

    public String getProvisionalReceiptNo() {
        return provisionalReceiptNo;
    }

    public void setProvisionalReceiptNo(String provisionalReceiptNo) {
        this.provisionalReceiptNo = provisionalReceiptNo;
    }

    public IncomeExpenseHead getIncomeExpenseHead() {
        return incomeExpenseHead;
    }

    public void setIncomeExpenseHead(IncomeExpenseHead incomeExpenseHead) {
        this.incomeExpenseHead = incomeExpenseHead;
    }

    public String getFormName() {
        return formName;
    }

    public void setFormName(String formName) {
        this.formName = formName;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }


    public boolean isMockLocationStatus() {
        return mockLocationStatus;
    }

    public void setMockLocationStatus(boolean mockLocationStatus) {
        this.mockLocationStatus = mockLocationStatus;
    }

    public String getReceiptRemarks() {
        return receiptRemarks;
    }

    public void setReceiptRemarks(String receiptRemarks) {
        this.receiptRemarks = receiptRemarks;
    }

    public DocumentType getDocumentType() {
        return documentType;
    }

    public void setDocumentType(DocumentType documentType) {
        this.documentType = documentType;
    }


    public void setActivityPid(String activityPid) {
        this.activityPid = activityPid;
    }

    public String getDocumentPid() {
        return documentPid;
    }

    public void setDocumentPid(String documentPid) {
        this.documentPid = documentPid;
    }

    public String getActivityPid() {
        return activityPid;
    }

    public boolean isWithCustomer() {
        return withCustomer;
    }

    public void setWithCustomer(boolean withCustomer) {
        this.withCustomer = withCustomer;
    }

    public String getDocumentNumberServer() {
        return documentNumberServer;
    }

    public void setDocumentNumberServer(String documentNumberServer) {
        this.documentNumberServer = documentNumberServer;
    }

    public String getFormElementName() {
        return formElementName;
    }

    public void setFormElementName(String formElementName) {
        this.formElementName = formElementName;
    }

    public String getFormElementPid() {
        return formElementPid;
    }

    public void setFormElementPid(String formElementPid) {
        this.formElementPid = formElementPid;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getInventoryPid() {
        return inventoryPid;
    }

    public void setInventoryPid(String inventoryPid) {
        this.inventoryPid = inventoryPid;
    }

    public String getAccountingPid() {
        return accountingPid;
    }

    public void setAccountingPid(String accountingPid) {
        this.accountingPid = accountingPid;
    }

    public String getDynamicPid() {
        return dynamicPid;
    }

    public void setDynamicPid(String dynamicPid) {
        this.dynamicPid = dynamicPid;
    }

    public String getFormElementType() {
        return formElementType;
    }

    public void setFormElementType(String formElementType) {
        this.formElementType = formElementType;
    }
}

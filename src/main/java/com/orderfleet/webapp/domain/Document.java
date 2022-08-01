package com.orderfleet.webapp.domain;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.orderfleet.webapp.domain.enums.AccountTypeColumn;
import com.orderfleet.webapp.domain.enums.DocumentType;
import com.orderfleet.webapp.domain.enums.PaymentMode;
import com.orderfleet.webapp.domain.enums.StockFlow;
import com.orderfleet.webapp.domain.enums.VoucherNumberGenerationType;

/**
 * A Document.
 * 
 * @author Shaheer
 * @since June 20, 2016
 */
@Entity
@Table(name = "tbl_document")
public class Document implements Serializable, Cloneable {

	private static final long serialVersionUID = 1L;

	@Id
	@GenericGenerator(name = "seq_document_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "seq_document_id") })
	@GeneratedValue(generator = "seq_document_id_GEN")
	@Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_document_id')")
	private Long id;

	@NotNull
	@Column(name = "pid", unique = true, nullable = false, updatable = false)
	private String pid;

	@NotNull
	@Size(min = 1, max = 255)
	@Column(name = "name", length = 255, nullable = false)
	private String name;

	@NotNull
	@Size(min = 1, max = 10)
	@Column(name = "document_prefix", length = 10, nullable = false)
	private String documentPrefix;

	@Size(max = 55)
	@Column(name = "alias", length = 55)
	private String alias;

	@Column(name = "description")
	private String description;

	@Column(name = "terms_and_conditions", length = 15000)
	private String termsAndConditions;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "document_type", nullable = false)
	private DocumentType documentType;

	@Enumerated(EnumType.STRING)
	@Column(name = "activity_account")
	private AccountTypeColumn activityAccount;

	@Column(name = "save", nullable = false)
	private boolean save = true;

	@Column(name = "editable", nullable = false, columnDefinition = "boolean DEFAULT 'FALSE'")
	private boolean editable = true;

	@Column(name = "batch_enabled", nullable = false, columnDefinition = "boolean DEFAULT 'FALSE'")
	private boolean batchEnabled = false;

	@ManyToOne()
	@NotNull
	private Company company;

	@Column(name = "prompt_stock_location", nullable = false, columnDefinition = "boolean DEFAULT 'FALSE'")
	private boolean promptStockLocation = false;

	@Column(name = "single_voucher_mode", nullable = false, columnDefinition = "boolean DEFAULT 'FALSE'")
	private boolean singleVoucherMode;

	@Column(name = "photo_mandatory", nullable = false, columnDefinition = "boolean DEFAULT 'FALSE'")
	private boolean photoMandatory;

	@Column(name = "is_take_image_from_gallery", nullable = false, columnDefinition = "boolean DEFAULT 'FALSE'")
	private boolean isTakeImageFromGallery;

	@Column(name = "created_date", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	@JsonIgnore
	private LocalDateTime createdDate = LocalDateTime.now();

	@Column(name = "last_modified_date", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	@JsonIgnore
	private LocalDateTime lastModifiedDate = LocalDateTime.now();

	@Enumerated(EnumType.STRING)
	@Column(name = "payment_mode")
	private PaymentMode mode;

	@Enumerated(EnumType.STRING)
	@Column(name = "stock_flow")
	private StockFlow stockFlow;

	@Column(name = "qrcode_enabled", nullable = false, columnDefinition = "boolean DEFAULT 'FALSE'")
	private boolean qrCodeEnabled;

	@Column(name = "order_no_enabled", nullable = false, columnDefinition = "boolean DEFAULT 'FALSE'")
	private boolean orderNoEnabled;

	@Column(name = "add_new_customer", nullable = false, columnDefinition = "boolean DEFAULT 'TRUE'")
	private boolean addNewCustomer;

	@Column(name = "terms_and_conditions_column", nullable = false, columnDefinition = "boolean DEFAULT 'FALSE'")
	private boolean termsAndConditionsColumn;

	@Column(name = "has_telephonic_order", nullable = false, columnDefinition = "boolean DEFAULT 'FALSE'")
	private boolean hasTelephonicOrder;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "voucher_number_generation_type", nullable = false, columnDefinition = "character varying DEFAULT 'TYPE_1'")
	private VoucherNumberGenerationType voucherNumberGenerationType;

	@Size(max = 5000000)
	@Lob
	@Column(name = "header_image")
	private byte[] headerImage;

	@Column(name = "header_image_content_type", length = 255)
	private String headerImageContentType;

	@Size(max = 5000000)
	@Lob
	@Column(name = "footer_image")
	private byte[] footerImage;

	@Column(name = "footer_image_content_type", length = 255)
	private String footerImageContentType;

	@Column(name = "rate_With_Tax", nullable = false, columnDefinition = "boolean DEFAULT 'FALSE'")
	private boolean rateWithTax;
	
	@Column(name = "sms_api_enable", nullable = false, columnDefinition = "boolean DEFAULT 'FALSE'")
	private boolean smsApiEnable;
	
	@Column(name = "discount_Scale_Bar", nullable = false, columnDefinition = "boolean DEFAULT 'FALSE'")
	private boolean discountScaleBar;
	
	@Column(name = "discount_percentage", columnDefinition = "double precision DEFAULT 0")
	private double discountPercentage ;
	
	@Column(name = "enable_header_printOut", nullable = false, columnDefinition = "boolean DEFAULT 'FALSE'")
	private boolean enableHeaderPrintOut;

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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDocumentPrefix() {
		return documentPrefix;
	}

	public void setDocumentPrefix(String documentPrefix) {
		this.documentPrefix = documentPrefix;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public DocumentType getDocumentType() {
		return documentType;
	}

	public void setDocumentType(DocumentType documentType) {
		this.documentType = documentType;
	}

	public AccountTypeColumn getActivityAccount() {
		return activityAccount;
	}

	public void setActivityAccount(AccountTypeColumn activityAccount) {
		this.activityAccount = activityAccount;
	}

	public boolean getSave() {
		return save;
	}

	public void setSave(boolean save) {
		this.save = save;
	}

	public boolean getEditable() {
		return editable;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
	}

	public boolean getBatchEnabled() {
		return batchEnabled;
	}

	public void setBatchEnabled(boolean batchEnabled) {
		this.batchEnabled = batchEnabled;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public String getHeaderImageContentType() {
		return headerImageContentType;
	}

	public void setHeaderImageContentType(String headerImageContentType) {
		this.headerImageContentType = headerImageContentType;
	}

	public String getFooterImageContentType() {
		return footerImageContentType;
	}

	public void setFooterImageContentType(String footerImageContentType) {
		this.footerImageContentType = footerImageContentType;
	}

	public boolean getPromptStockLocation() {
		return promptStockLocation;
	}

	public void setPromptStockLocation(boolean promptStockLocation) {
		this.promptStockLocation = promptStockLocation;
	}

	public boolean getSingleVoucherMode() {
		return singleVoucherMode;
	}

	public void setSingleVoucherMode(boolean singleVoucherMode) {
		this.singleVoucherMode = singleVoucherMode;
	}

	public boolean getPhotoMandatory() {
		return photoMandatory;
	}

	public void setPhotoMandatory(boolean photoMandatory) {
		this.photoMandatory = photoMandatory;
	}

	public LocalDateTime getCreatedDate() {
		return createdDate;
	}

	public LocalDateTime getLastModifiedDate() {
		return lastModifiedDate;
	}

	public boolean getIsTakeImageFromGallery() {
		return isTakeImageFromGallery;
	}

	public void setIsTakeImageFromGallery(boolean isTakeImageFromGallery) {
		this.isTakeImageFromGallery = isTakeImageFromGallery;
	}

	public PaymentMode getMode() {
		return mode;
	}

	public void setMode(PaymentMode mode) {
		this.mode = mode;
	}

	public StockFlow getStockFlow() {
		return stockFlow;
	}

	public void setStockFlow(StockFlow stockFlow) {
		this.stockFlow = stockFlow;
	}

	public boolean getQrCodeEnabled() {
		return qrCodeEnabled;
	}

	public void setQrCodeEnabled(boolean qrCodeEnabled) {
		this.qrCodeEnabled = qrCodeEnabled;
	}

	public boolean getOrderNoEnabled() {
		return orderNoEnabled;
	}

	public void setOrderNoEnabled(boolean orderNoEnabled) {
		this.orderNoEnabled = orderNoEnabled;
	}

	public VoucherNumberGenerationType getVoucherNumberGenerationType() {
		return voucherNumberGenerationType;
	}

	public void setVoucherNumberGenerationType(VoucherNumberGenerationType voucherNumberGenerationType) {
		this.voucherNumberGenerationType = voucherNumberGenerationType;
	}

	public boolean getAddNewCustomer() {
		return addNewCustomer;
	}

	public void setAddNewCustomer(boolean addNewCustomer) {
		this.addNewCustomer = addNewCustomer;
	}

	public String getTermsAndConditions() {
		return termsAndConditions;
	}

	public void setTermsAndConditions(String termsAndConditions) {
		this.termsAndConditions = termsAndConditions;
	}

	public boolean isTermsAndConditionsColumn() {
		return termsAndConditionsColumn;
	}

	public void setTermsAndConditionsColumn(boolean termsAndConditionsColumn) {
		this.termsAndConditionsColumn = termsAndConditionsColumn;
	}

	public boolean getHasTelephonicOrder() {
		return hasTelephonicOrder;
	}

	public void setHasTelephonicOrder(boolean hasTelephonicOrder) {
		this.hasTelephonicOrder = hasTelephonicOrder;
	}

	public boolean getRateWithTax() {
		return rateWithTax;
	}

	public void setRateWithTax(boolean rateWithTax) {
		this.rateWithTax = rateWithTax;
	}

	
	
	public boolean getDiscountScaleBar() {
		return discountScaleBar;
	}

	public void setDiscountScaleBar(boolean discountScaleBar) {
		this.discountScaleBar = discountScaleBar;
	}

	public byte[] getHeaderImage() {
		return headerImage;
	}

	public void setHeaderImage(byte[] headerImage) {
		this.headerImage = headerImage;
	}

	public byte[] getFooterImage() {
		return footerImage;
	}

	public void setFooterImage(byte[] footerImage) {
		this.footerImage = footerImage;
	}

	
	public double getDiscountPercentage() {
		return discountPercentage;
	}

	public void setDiscountPercentage(double discountPercentage) {
		this.discountPercentage = discountPercentage;
	}
	
	

	public boolean getSmsApiEnable() {
		return smsApiEnable;
	}

	public void setSmsApiEnable(boolean smsApiEnable) {
		this.smsApiEnable = smsApiEnable;
	}

	public boolean getEnableHeaderPrintOut() {
		return enableHeaderPrintOut;
	}

	public void setEnableHeaderPrintOut(boolean enableHeaderPrintOut) {
		this.enableHeaderPrintOut = enableHeaderPrintOut;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		Document document = (Document) o;
		if (document.id == null || id == null) {
			return false;
		}
		return Objects.equals(id, document.id);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(id);
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
}

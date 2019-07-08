package com.orderfleet.webapp.web.rest.dto;

import java.time.LocalDateTime;
import java.util.Objects;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.orderfleet.webapp.domain.Document;
import com.orderfleet.webapp.domain.enums.AccountTypeColumn;
import com.orderfleet.webapp.domain.enums.DocumentType;
import com.orderfleet.webapp.domain.enums.PaymentMode;
import com.orderfleet.webapp.domain.enums.StockFlow;

/**
 * A DTO for the Document entity.
 * 
 * @author Muhammed Riyas T
 * @since June 21, 2016
 */
public class DocumentDTO implements Cloneable {

	private String pid;

	@NotNull
	@Size(min = 1, max = 255)
	private String name;

	@NotNull
	@Size(min = 1, max = 10)
	private String documentPrefix;

	@Size(max = 55)
	private String alias;

	private String description;

	private DocumentType documentType;

	private AccountTypeColumn activityAccount;

	private boolean save;

	private String sourceStockLocationPid;

	private String sourceStockLocationName;

	private String destinationStockLocationPid;

	private String destinationStockLocationName;

	private boolean editable = true;

	private boolean batchEnabled;

	private boolean promptStockLocation;

	private boolean activityDocRequired;

	private int activityDocSortOrder;

	private boolean singleVoucherMode;

	private boolean photoMandatory;

	private boolean isTakeImageFromGallery;

	private LocalDateTime lastModifiedDate;

	private String companyName;

	private String companyPid;

	private boolean imageOption = false;
	
	private PaymentMode mode;
	
	private StockFlow stockFlow;
	
	private boolean qrCodeEnabled;
	
	private boolean orderNoEnabled;
	

	public DocumentDTO() {

	}

	public DocumentDTO(Document document) {
		super();
		this.pid = document.getPid();
		this.name = document.getName();
		this.documentPrefix = document.getDocumentPrefix();
		this.alias = document.getAlias();
		this.description = document.getDescription();
		this.documentType = document.getDocumentType();
		this.activityAccount = document.getActivityAccount();
		this.save = document.getSave();
		this.editable = document.getEditable();
		this.batchEnabled = document.getBatchEnabled();
		this.promptStockLocation = document.getPromptStockLocation();
		this.singleVoucherMode = document.getSingleVoucherMode();
		this.photoMandatory = document.getPhotoMandatory();
		this.isTakeImageFromGallery = document.getIsTakeImageFromGallery();
		this.lastModifiedDate = document.getLastModifiedDate();
		this.companyPid = document.getCompany().getPid();
		this.companyName = document.getCompany().getLegalName();
		this.mode = document.getMode();
		this.stockFlow = document.getStockFlow() == null ? StockFlow.NONE : document.getStockFlow();
		this.qrCodeEnabled = document.getQrCodeEnabled();
		this.orderNoEnabled = document.getOrderNoEnabled();
	}

	public DocumentDTO(Document document, boolean activityDocRequired, int activityDocSortOrder) {
		super();
		this.pid = document.getPid();
		this.name = document.getName();
		this.documentPrefix = document.getDocumentPrefix();
		this.alias = document.getAlias();
		this.description = document.getDescription();
		this.documentType = document.getDocumentType();
		this.activityAccount = document.getActivityAccount();
		this.save = document.getSave();
		this.editable = document.getEditable();
		this.batchEnabled = document.getBatchEnabled();
		this.promptStockLocation = document.getPromptStockLocation();
		this.activityDocRequired = activityDocRequired;
		this.activityDocSortOrder = activityDocSortOrder;
		this.singleVoucherMode = document.getSingleVoucherMode();
		this.photoMandatory = document.getPhotoMandatory();
		this.isTakeImageFromGallery = document.getIsTakeImageFromGallery();
		this.lastModifiedDate = document.getLastModifiedDate();
		this.companyPid = document.getCompany().getPid();
		this.companyName = document.getCompany().getLegalName();
		this.mode = document.getMode();
		this.stockFlow = document.getStockFlow() == null ? StockFlow.NONE : document.getStockFlow();
		this.qrCodeEnabled = document.getQrCodeEnabled();
		this.orderNoEnabled = document.getOrderNoEnabled();
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

	public String getDocumentPrefix() {
		return documentPrefix;
	}

	public void setDocumentPrefix(String documentPrefix) {
		this.documentPrefix = documentPrefix;
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

	public String getSourceStockLocationPid() {
		return sourceStockLocationPid;
	}

	public void setSourceStockLocationPid(String sourceStockLocationPid) {
		this.sourceStockLocationPid = sourceStockLocationPid;
	}

	public String getSourceStockLocationName() {
		return sourceStockLocationName;
	}

	public void setSourceStockLocationName(String sourceStockLocationName) {
		this.sourceStockLocationName = sourceStockLocationName;
	}

	public String getDestinationStockLocationPid() {
		return destinationStockLocationPid;
	}

	public void setDestinationStockLocationPid(String destinationStockLocationPid) {
		this.destinationStockLocationPid = destinationStockLocationPid;
	}

	public String getDestinationStockLocationName() {
		return destinationStockLocationName;
	}

	public void setDestinationStockLocationName(String destinationStockLocationName) {
		this.destinationStockLocationName = destinationStockLocationName;
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

	public boolean isPromptStockLocation() {
		return promptStockLocation;
	}

	public void setPromptStockLocation(boolean promptStockLocation) {
		this.promptStockLocation = promptStockLocation;
	}

	public boolean getActivityDocRequired() {
		return activityDocRequired;
	}

	public void setActivityDocRequired(boolean activityDocRequired) {
		this.activityDocRequired = activityDocRequired;
	}

	public int getActivityDocSortOrder() {
		return activityDocSortOrder;
	}

	public void setActivityDocSortOrder(int activityDocSortOrder) {
		this.activityDocSortOrder = activityDocSortOrder;
	}

	public boolean getPhotoMandatory() {
		return photoMandatory;
	}

	public void setPhotoMandatory(boolean photoMandatory) {
		this.photoMandatory = photoMandatory;
	}

	public boolean getSingleVoucherMode() {
		return singleVoucherMode;
	}

	public void setSingleVoucherMode(boolean singleVoucherMode) {
		this.singleVoucherMode = singleVoucherMode;
	}

	public boolean getIsTakeImageFromGallery() {
		return isTakeImageFromGallery;
	}

	public void setIsTakeImageFromGallery(boolean isTakeImageFromGallery) {
		this.isTakeImageFromGallery = isTakeImageFromGallery;
	}

	public LocalDateTime getLastModifiedDate() {
		return lastModifiedDate;
	}

	public void setLastModifiedDate(LocalDateTime lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
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

	public boolean getImageOption() {
		return imageOption;
	}

	public void setImageOption(boolean imageOption) {
		this.imageOption = imageOption;
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

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		DocumentDTO documentDTO = (DocumentDTO) o;

		if (!Objects.equals(pid, documentDTO.pid))
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(pid);
	}

	@Override
	public String toString() {
		return "DocumentDTO [pid=" + pid + ", name=" + name + ", documentPrefix=" + documentPrefix + ", alias=" + alias
				+ ", description=" + description + ", documentType=" + documentType + ", activityAccount="
				+ activityAccount + ", save=" + save + ", sourceStockLocationPid=" + sourceStockLocationPid
				+ ", sourceStockLocationName=" + sourceStockLocationName + ", destinationStockLocationPid="
				+ destinationStockLocationPid + ", destinationStockLocationName=" + destinationStockLocationName
				+ ", editable=" + editable + ", batchEnabled=" + batchEnabled + ", promptStockLocation="
				+ promptStockLocation + ", activityDocRequired=" + activityDocRequired + ", activityDocSortOrder="
				+ activityDocSortOrder + ", singleVoucherMode=" + singleVoucherMode + ", photoMandatory="
				+ photoMandatory + ", isTakeImageFromGallery=" + isTakeImageFromGallery + ", lastModifiedDate="
				+ lastModifiedDate + ", companyName=" + companyName + ", companyPid=" + companyPid + ", imageOption="
				+ imageOption + ", mode=" + mode + ", stockFlow=" + stockFlow + ", qrCodeEnabled=" + qrCodeEnabled
				+ "]";
	}

	

}

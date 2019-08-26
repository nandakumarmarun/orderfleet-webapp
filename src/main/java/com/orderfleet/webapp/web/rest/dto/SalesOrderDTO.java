package com.orderfleet.webapp.web.rest.dto;

import java.time.LocalDateTime;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import com.orderfleet.webapp.domain.InventoryVoucherHeader;
import com.orderfleet.webapp.web.tally.dto.GstLedgerDTO;

/**
 * A DTO For Generate SalesOrder To Client APP.
 *
 * @author Sarath
 * @since Oct 28, 2016
 */
@XmlRootElement
public class SalesOrderDTO {

	private Long id;
	private String inventoryVoucherHeaderPid;
	private String ledgerName;
	private String ledgerAddress;
	private String ledgerState;
	private String ledgerCountry;
	private String ledgerGstType;
	private String priceLevel;
	private String narration;
	private String selectedVats;
	private String tabOrderNumber;
	private String documentName;
	private String documentAlias;
	private double docDiscountAmount;
	private double docDiscountPercentage;
	private String trimChar;
	private String employeeAlias;

	private AccountProfileDTO accountProfileDTO;
	private List<SalesOrderItemDTO> salesOrderItemDTOs;
	private List<VatLedgerDTO> vatLedgerDTOs;
	private LocalDateTime date;
	private List<DynamicDocumentHeaderDTO> dynamicDocumentHeaderDTOs;
	private InventoryVoucherHeaderDTO inventoryVoucherHeaderDTO = new InventoryVoucherHeaderDTO();
	private List<GstLedgerDTO> gstLedgerDtos;

	public SalesOrderDTO() {
		super();
	}

	public SalesOrderDTO(InventoryVoucherHeader inventoryVoucherHeader) {
		super();
		this.inventoryVoucherHeaderPid = inventoryVoucherHeader.getPid();
		this.id = inventoryVoucherHeader.getId();
		this.ledgerName = inventoryVoucherHeader.getReceiverAccount().getName();
		this.trimChar = inventoryVoucherHeader.getReceiverAccount().getTrimChar();
		this.ledgerAddress = inventoryVoucherHeader.getReceiverAccount().getAddress();
		this.date = inventoryVoucherHeader.getDocumentDate();
		this.documentName = inventoryVoucherHeader.getDocument().getName();
		this.documentAlias = inventoryVoucherHeader.getDocument().getAlias();
		this.docDiscountAmount = inventoryVoucherHeader.getDocDiscountAmount();
		this.docDiscountPercentage = inventoryVoucherHeader.getDocDiscountPercentage();
		if (inventoryVoucherHeader.getReceiverAccount().getDefaultPriceLevel() != null) {
			this.priceLevel = inventoryVoucherHeader.getReceiverAccount().getDefaultPriceLevel().getName();
		}
		this.inventoryVoucherHeaderDTO = new InventoryVoucherHeaderDTO(inventoryVoucherHeader);
		this.ledgerState = inventoryVoucherHeader.getReceiverAccount().getStateName();
		this.ledgerCountry = inventoryVoucherHeader.getReceiverAccount().getCountryName();
		this.ledgerGstType = inventoryVoucherHeader.getReceiverAccount().getGstRegistrationType();
		if(inventoryVoucherHeader.getEmployee() != null) {
			this.employeeAlias = inventoryVoucherHeader.getEmployee().getAlias();
		}
		
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getLedgerName() {
		return ledgerName;
	}

	public void setLedgerName(String ledgerName) {
		this.ledgerName = ledgerName;
	}

	public String getLedgerAddress() {
		return ledgerAddress;
	}

	public void setLedgerAddress(String ledgerAddress) {
		this.ledgerAddress = ledgerAddress;
	}

	public String getPriceLevel() {
		return priceLevel;
	}

	public void setPriceLevel(String priceLevel) {
		this.priceLevel = priceLevel;
	}

	public LocalDateTime getDate() {
		return date;
	}

	public void setDate(LocalDateTime date) {
		this.date = date;
	}

	public String getNarration() {
		return narration;
	}

	public void setNarration(String narration) {
		this.narration = narration;
	}

	public String getSelectedVats() {
		return selectedVats;
	}

	public void setSelectedVats(String selectedVats) {
		this.selectedVats = selectedVats;
	}

	public String getTabOrderNumber() {
		return tabOrderNumber;
	}

	public void setTabOrderNumber(String tabOrderNumber) {
		this.tabOrderNumber = tabOrderNumber;
	}

	public List<SalesOrderItemDTO> getSalesOrderItemDTOs() {
		return salesOrderItemDTOs;
	}

	public void setSalesOrderItemDTOs(List<SalesOrderItemDTO> salesOrderItemDTOs) {
		this.salesOrderItemDTOs = salesOrderItemDTOs;
	}

	public List<VatLedgerDTO> getVatLedgerDTOs() {
		return vatLedgerDTOs;
	}

	public void setVatLedgerDTOs(List<VatLedgerDTO> vatLedgerDTOs) {
		this.vatLedgerDTOs = vatLedgerDTOs;
	}

	public String getInventoryVoucherHeaderPid() {
		return inventoryVoucherHeaderPid;
	}

	public void setInventoryVoucherHeaderPid(String inventoryVoucherHeaderPid) {
		this.inventoryVoucherHeaderPid = inventoryVoucherHeaderPid;
	}

	public String getDocumentName() {
		return documentName;
	}

	public void setDocumentName(String documentName) {
		this.documentName = documentName;
	}
	
	public String getDocumentAlias() {
		return documentAlias;
	}

	public void setDocumentAlias(String documentAlias) {
		this.documentAlias = documentAlias;
	}

	public List<DynamicDocumentHeaderDTO> getDynamicDocumentHeaderDTOs() {
		return dynamicDocumentHeaderDTOs;
	}

	public void setDynamicDocumentHeaderDTOs(List<DynamicDocumentHeaderDTO> dynamicDocumentHeaderDTOs) {
		this.dynamicDocumentHeaderDTOs = dynamicDocumentHeaderDTOs;
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

	public InventoryVoucherHeaderDTO getInventoryVoucherHeaderDTO() {
		return inventoryVoucherHeaderDTO;
	}

	public void setInventoryVoucherHeaderDTO(InventoryVoucherHeaderDTO inventoryVoucherHeaderDTO) {
		this.inventoryVoucherHeaderDTO = inventoryVoucherHeaderDTO;
	}

	public AccountProfileDTO getAccountProfileDTO() {
		return accountProfileDTO;
	}

	public void setAccountProfileDTO(AccountProfileDTO accountProfileDTO) {
		this.accountProfileDTO = accountProfileDTO;
	}

	public String getTrimChar() {
		return trimChar;
	}

	public void setTrimChar(String trimChar) {
		this.trimChar = trimChar;
	}
	
	public List<GstLedgerDTO> getGstLedgerDtos() {
		return gstLedgerDtos;
	}

	public void setGstLedgerDtos(List<GstLedgerDTO> gstLedgerDtos) {
		this.gstLedgerDtos = gstLedgerDtos;
	}
	
	public String getLedgerState() {
		return ledgerState;
	}

	public void setLedgerState(String ledgerState) {
		this.ledgerState = ledgerState;
	}

	public String getLedgerCountry() {
		return ledgerCountry;
	}

	public void setLedgerCountry(String ledgerCountry) {
		this.ledgerCountry = ledgerCountry;
	}
	
	public String getLedgerGstType() {
		return ledgerGstType;
	}

	public void setLedgerGstType(String ledgerGstType) {
		this.ledgerGstType = ledgerGstType;
	}

	public String getEmployeeAlias() {
		return employeeAlias;
	}

	public void setEmployeeAlias(String employeeAlias) {
		this.employeeAlias = employeeAlias;
	}

	@Override
	public String toString() {
		return "SalesOrderDTO [ledgerName=" + ledgerName + ", ledgerAddress=" + ledgerAddress + ", priceLevel="
				+ priceLevel + ", date=" + date + ", narration=" + narration + ", selectedVats=" + selectedVats
				+ ", tabOrderNumber=" + tabOrderNumber + ", salesOrderItemDTOs=" + salesOrderItemDTOs + "]";
	}

}

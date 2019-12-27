package com.orderfleet.webapp.web.rest.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.persistence.Column;
import javax.persistence.Lob;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.orderfleet.webapp.domain.ProductProfile;
import com.orderfleet.webapp.domain.enums.StockAvailabilityStatus;

/**
 * A DTO for the ProductProfile entity.
 *
 * @author Shaheer
 * @since May 17, 2016
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductProfileDTO {

	private String pid;

	@NotNull
	@Size(max = 255)
	private String name;

	@Size(max = 55)
	private String alias;

	private String description;

	@NotNull
	private BigDecimal price;

	private double mrp;

	private String sku;

	private Double unitQty;

	private double taxRate;

	private String productCategoryPid;

	private String productCategoryName;

	private String divisionPid;

	private String divisionName;

	@Size(max = 50000)
	@Lob
	private byte[] colorImage;

	private String colorImageContentType;

	private String size;

	private String filesPid;

	private boolean activated;

	private LocalDateTime lastModifiedDate;

	private String defaultLedger;

	private List<TaxMasterDTO> productProfileTaxMasterDTOs;

	private StockAvailabilityStatus stockAvailabilityStatus = StockAvailabilityStatus.AVAILABLE;

	private String trimChar;

	private String hsnCode;

	private String productDescription;

	private String barcode;

	private String remarks;

	public ProductProfileDTO() {
		super();
	}

	// public ProductProfileDTO(ProductProfile productProfile) {
	// this(productProfile.getPid(), productProfile.getName(),
	// productProfile.getAlias(),
	// productProfile.getDescription(), productProfile.getPrice(),
	// productProfile.getMrp(),
	// productProfile.getSku(), productProfile.getUnitQty(),
	// productProfile.getTaxRate(),
	// productProfile.getProductCategory().getPid(),
	// productProfile.getProductCategory().getName(),
	// productProfile.getDivision().getPid(),
	// productProfile.getDivision().getName(),
	// productProfile.getColorImage(),
	// productProfile.getColorImageContentType(), productProfile.getSize(),
	// productProfile.getActivated(), productProfile.getLastModifiedDate(),
	// productProfile.getDefaultLedger());
	// }

	// this.inventoryVoucherDetails=inventoryVoucherHeader.getInventoryVoucherDetails().stream().map(InventoryVoucherDetailDTO::new)
	// .collect(Collectors.toList());

	public ProductProfileDTO(ProductProfile profile) {
		super();

		this.pid = profile.getPid();
		this.name = profile.getName();
		this.alias = profile.getAlias();
		this.description = profile.getDescription();
		this.price = profile.getPrice();
		this.mrp = profile.getMrp();
		this.sku = profile.getSku();
		this.unitQty = profile.getUnitQty();
		this.taxRate = profile.getTaxRate();
		this.productCategoryPid = profile.getProductCategory().getPid();
		this.productCategoryName = profile.getProductCategory().getName();
		this.divisionPid = profile.getDivision().getPid();
		this.divisionName = profile.getDivision().getName();
		this.colorImage = profile.getColorImage();
		this.colorImageContentType = profile.getColorImageContentType();
		this.size = profile.getSize() == null || profile.getSize().equalsIgnoreCase("") ? null : profile.getSize();
		this.activated = profile.getActivated();
		this.lastModifiedDate = profile.getLastModifiedDate();
		this.defaultLedger = profile.getDefaultLedger();
		this.stockAvailabilityStatus = profile.getStockAvailabilityStatus();
		this.trimChar = profile.getTrimChar();
		this.hsnCode = profile.getHsnCode();
		this.productDescription = profile.getProductDescription();
		this.barcode = profile.getBarcode();
		this.remarks = profile.getRemarks();

		List<TaxMasterDTO> taxMasterDTOs = new ArrayList<>();

		this.productProfileTaxMasterDTOs = profile.getTaxMastersList() == null || profile.getTaxMastersList().isEmpty()
				|| profile.getTaxMastersList().size() <= 0 ? taxMasterDTOs
						: profile.getTaxMastersList().stream().map(TaxMasterDTO::new).collect(Collectors.toList());

		if (profile.getFiles() != null || !profile.getFiles().isEmpty()) {
			String filePids = profile.getFiles().stream().map(fileDTO -> fileDTO.getPid())
					.collect(Collectors.joining(","));
			this.filesPid = filePids;
		}

	}

	public ProductProfileDTO(String pid, String name, String alias, String description, BigDecimal price, double mrp,
			String sku, Double unitQty, double taxRate, String productCategoryPid, String productCategoryName,
			String divisionPid, String divisionName, byte[] colorImage, String colorImageContentType, String size,
			String filesPid, boolean activated, LocalDateTime lastModifiedDate, String defaultLedger,
			List<TaxMasterDTO> productProfileTaxMasterDTOs, StockAvailabilityStatus stockAvailabilityStatus) {
		super();
		this.pid = pid;
		this.name = name;
		this.alias = alias;
		this.description = description;
		this.price = price;
		this.mrp = mrp;
		this.sku = sku;
		this.unitQty = unitQty;
		this.taxRate = taxRate;
		this.productCategoryPid = productCategoryPid;
		this.productCategoryName = productCategoryName;
		this.divisionPid = divisionPid;
		this.divisionName = divisionName;
		this.colorImage = colorImage;
		this.colorImageContentType = colorImageContentType;
		this.size = size;
		this.filesPid = filesPid;
		this.activated = activated;
		this.lastModifiedDate = lastModifiedDate;
		this.defaultLedger = defaultLedger;
		this.productProfileTaxMasterDTOs = productProfileTaxMasterDTOs;
		this.stockAvailabilityStatus = stockAvailabilityStatus;
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

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public double getMrp() {
		return mrp;
	}

	public void setMrp(double mrp) {
		this.mrp = mrp;
	}

	public String getSku() {
		return sku;
	}

	public void setSku(String sku) {
		this.sku = sku;
	}

	public Double getUnitQty() {
		return unitQty;
	}

	public void setUnitQty(Double unitQty) {
		this.unitQty = unitQty;
	}

	public double getTaxRate() {
		return taxRate;
	}

	public void setTaxRate(double taxRate) {
		this.taxRate = taxRate;
	}

	public String getProductCategoryPid() {
		return productCategoryPid;
	}

	public void setProductCategoryPid(String productCategoryPid) {
		this.productCategoryPid = productCategoryPid;
	}

	public String getProductCategoryName() {
		return productCategoryName;
	}

	public void setProductCategoryName(String productCategoryName) {
		this.productCategoryName = productCategoryName;
	}

	public String getDivisionPid() {
		return divisionPid;
	}

	public void setDivisionPid(String divisionPid) {
		this.divisionPid = divisionPid;
	}

	public String getDivisionName() {
		return divisionName;
	}

	public void setDivisionName(String divisionName) {
		this.divisionName = divisionName;
	}

	public byte[] getColorImage() {
		return colorImage;
	}

	public void setColorImage(byte[] colorImage) {
		this.colorImage = colorImage;
	}

	public String getColorImageContentType() {
		return colorImageContentType;
	}

	public void setColorImageContentType(String colorImageContentType) {
		this.colorImageContentType = colorImageContentType;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public String getFilesPid() {
		return filesPid;
	}

	public void setFilesPid(String filesPid) {
		this.filesPid = filesPid;
	}

	public boolean getActivated() {
		return activated;
	}

	public void setActivated(boolean activated) {
		this.activated = activated;
	}

	public LocalDateTime getLastModifiedDate() {
		return lastModifiedDate;
	}

	public void setLastModifiedDate(LocalDateTime lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}

	public String getDefaultLedger() {
		return defaultLedger;
	}

	public void setDefaultLedger(String defaultLedger) {
		this.defaultLedger = defaultLedger;
	}

	public List<TaxMasterDTO> getProductProfileTaxMasterDTOs() {
		return productProfileTaxMasterDTOs;
	}

	public void setProductProfileTaxMasterDTOs(List<TaxMasterDTO> productProfileTaxMasterDTOs) {
		this.productProfileTaxMasterDTOs = productProfileTaxMasterDTOs;
	}

	public StockAvailabilityStatus getStockAvailabilityStatus() {
		return stockAvailabilityStatus;
	}

	public void setStockAvailabilityStatus(StockAvailabilityStatus stockAvailabilityStatus) {
		this.stockAvailabilityStatus = stockAvailabilityStatus;
	}

	public String getHsnCode() {
		return hsnCode;
	}

	public void setHsnCode(String hsnCode) {
		this.hsnCode = hsnCode;
	}

	public String getTrimChar() {
		return trimChar;
	}

	public void setTrimChar(String trimChar) {
		this.trimChar = trimChar;
	}

	public String getProductDescription() {
		return productDescription;
	}

	public void setProductDescription(String productDescription) {
		this.productDescription = productDescription;
	}

	public String getBarcode() {
		return barcode;
	}

	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		ProductProfileDTO productProfileDTO = (ProductProfileDTO) o;

		if (!Objects.equals(pid, productProfileDTO.pid))
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(pid);
	}
}

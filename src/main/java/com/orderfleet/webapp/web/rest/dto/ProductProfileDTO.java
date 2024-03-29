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

	private Long alterId;
	
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

	private Double compoundUnitQty;

	private double taxRate;
	
	private double discountPercentage;

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

	private String unitsPid;

	private String unitsName;

	private List<TaxMasterDTO> productProfileTaxMasterDTOs;

	private StockAvailabilityStatus stockAvailabilityStatus = StockAvailabilityStatus.AVAILABLE;

	private String trimChar;

	private String hsnCode;

	private String productDescription;

	private String barcode;

	private String remarks;

	private String productId;

	private String productCode;

	private LocalDateTime createdDate;

	private String stockLocationName;

	private double stockQty;

	private String productGroup;

	private double purchaseCost;

	private String uploadSource;
	
	private double cessTaxRate;
	
	private double itemWidth;

	private double sellingRate;
	
	private String baseUnits;
	
	private double igst;

	private double rateConversion;
	
	private Double totalVolume;

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

	public String getStockLocationName() {
		return stockLocationName;
	}
	
	public double getDiscountPercentage() {
		return discountPercentage;
	}

	public void setDiscountPercentage(double discountPercentage) {
		this.discountPercentage = discountPercentage;
	}

	public void setStockLocationName(String stockLocationName) {
		this.stockLocationName = stockLocationName;
	}

	public double getStockQty() {
		return stockQty;
	}

	public void setStockQty(double stockQty) {
		this.stockQty = stockQty;
	}

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
		this.compoundUnitQty = profile.getCompoundUnitQty();
		this.taxRate = profile.getTaxRate();
		this.productCategoryPid = profile.getProductCategory().getPid();
		this.productCategoryName = profile.getProductCategory().getName();
		this.divisionPid = profile.getDivision().getPid();
		this.divisionName = profile.getDivision().getName();
		this.unitsPid = profile.getUnits() != null ? profile.getUnits().getPid() : "";
		this.unitsName = profile.getUnits() != null ? profile.getUnits().getName() : "";
		this.colorImage = profile.getColorImage();
		this.colorImageContentType = profile.getColorImageContentType();
		this.size = profile.getSize() == null || profile.getSize().equalsIgnoreCase("") ? null : profile.getSize();
		if (profile.getFiles() != null || !profile.getFiles().isEmpty()) {
			String filePids = profile.getFiles().stream().map(fileDTO -> fileDTO.getPid())
					.collect(Collectors.joining(","));
			this.filesPid = filePids;
		}

		this.activated = profile.getActivated();
		this.lastModifiedDate = profile.getLastModifiedDate();
		this.defaultLedger = profile.getDefaultLedger();
		this.stockAvailabilityStatus = profile.getStockAvailabilityStatus();
		this.trimChar = profile.getTrimChar();
		this.hsnCode = profile.getHsnCode();
		this.productDescription = profile.getProductDescription();
		this.barcode = profile.getBarcode();
		this.remarks = profile.getRemarks();
		this.productId = profile.getProductId();
		this.productCode = profile.getProductCode();
		this.productGroup = profile.getProductGroup();
		this.purchaseCost = profile.getPurchaseCost();
		this.itemWidth=profile.getWidth();
		this.sellingRate=profile.getSellingRate();
		this.baseUnits =profile.getBaseUnits();
		this.totalVolume =profile.getUnitQty()*profile.getCompoundUnitQty();
		List<TaxMasterDTO> taxMasterDTOs = new ArrayList<>();

		this.productProfileTaxMasterDTOs = profile.getTaxMastersList() == null || profile.getTaxMastersList().isEmpty()
				|| profile.getTaxMastersList().size() <= 0 ? taxMasterDTOs
						: profile.getTaxMastersList().stream().map(TaxMasterDTO::new).collect(Collectors.toList());

	}

	public ProductProfileDTO(String pid, String name, String alias, String description, BigDecimal price, double mrp,
			String sku, Double unitQty, Double compoundUnitQty, double taxRate, String productCategoryPid,
			String productCategoryName, String divisionPid, String divisionName, byte[] colorImage,
			String colorImageContentType, String size, String filesPid, boolean activated,
			LocalDateTime lastModifiedDate, String defaultLedger, List<TaxMasterDTO> productProfileTaxMasterDTOs,
			StockAvailabilityStatus stockAvailabilityStatus, String productGroup, String unitpid, String unitName,Double stockQty,Double totalVolume) {
		super();
		this.pid = pid;
		this.name = name;
		this.alias = alias;
		this.description = description;
		this.price = price;
		this.mrp = mrp;
		this.sku = sku;
		this.unitQty = unitQty;
		this.compoundUnitQty = compoundUnitQty;
		this.taxRate = taxRate;
		this.productCategoryPid = productCategoryPid;
		this.productCategoryName = productCategoryName;
		this.divisionPid = divisionPid;
		this.divisionName = divisionName;
		this.unitsPid = unitpid;
		this.unitsName = unitName;
		this.colorImage = colorImage;
		this.colorImageContentType = colorImageContentType;
		this.size = size;
		this.filesPid = filesPid;
		this.activated = activated;
		this.lastModifiedDate = lastModifiedDate;
		this.defaultLedger = defaultLedger;
		this.productProfileTaxMasterDTOs = productProfileTaxMasterDTOs;
		this.stockAvailabilityStatus = stockAvailabilityStatus;
		this.productGroup = productGroup;
		this.stockQty = stockQty;
		this.totalVolume = totalVolume;
	}

	public String getPid() {
		return pid;
	}

	public String getUnitsPid() {
		return unitsPid;
	}

	public void setUnitsPid(String unitsPid) {
		this.unitsPid = unitsPid;
	}

	public String getUnitsName() {
		return unitsName;
	}

	public void setUnitsName(String unitsName) {
		this.unitsName = unitsName;
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

	public Double getCompoundUnitQty() {
		return compoundUnitQty;
	}

	public void setCompoundUnitQty(Double compoundUnitQty) {
		this.compoundUnitQty = compoundUnitQty;
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

	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public LocalDateTime getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(LocalDateTime createdDate) {
		this.createdDate = createdDate;
	}

	public String getProductGroup() {
		return productGroup;
	}

	public void setProductGroup(String productGroup) {
		this.productGroup = productGroup;
	}

	public double getPurchaseCost() {
		return purchaseCost;
	}

	public void setPurchaseCost(double purchaseCost) {
		this.purchaseCost = purchaseCost;
	}

	public String getUploadSource() {
		return uploadSource;
	}

	public void setUploadSource(String uploadSource) {
		this.uploadSource = uploadSource;
	}
	
	

	public double getCessTaxRate() {
		return cessTaxRate;
	}

	public void setCessTaxRate(double cessTaxRate) {
		this.cessTaxRate = cessTaxRate;
	}
	
	public double getItemWidth() {
		return itemWidth;
	}

	public void setItemWidth(double itemWidth) {
		this.itemWidth = itemWidth;
	}

	public double getSellingRate() {
		return sellingRate;
	}

	public void setSellingRate(double sellingRate) {
		this.sellingRate = sellingRate;
	}

	public String getBaseUnits() {
		return baseUnits;
	}

	public void setBaseUnits(String baseUnits) {
		this.baseUnits = baseUnits;
	}

	public double getIgst() {
		return igst;
	}

	public void setIgst(double igst) {
		this.igst = igst;
	}

	public Long getAlterId() {
		return alterId;
	}

	public void setAlterId(Long alterId) {
		this.alterId = alterId;
	}

	public double getRateConversion() {
		return rateConversion;
	}

	public void setRateConversion(double rateConversion) {
		this.rateConversion = rateConversion;
	}

	public Double getTotalVolume() {
		return totalVolume;
	}

	public void setTotalVolume(Double totalVolume) {
		this.totalVolume = totalVolume;
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

package com.orderfleet.webapp.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

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
 * A InventoryVoucherDetail.
 * 
 * @author Muhammed Riyas T
 * @since July 18, 2016
 */
@Entity
@Table(name = "tbl_inventory_voucher_detail")
public class InventoryVoucherDetail implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GenericGenerator(name = "seq_inventory_voucher_detail_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "seq_inventory_voucher_detail_id") })
	@GeneratedValue(generator = "seq_inventory_voucher_detail_id_GEN")
	@Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_inventory_voucher_detail_id')")
	private Long id;

	// @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@ManyToOne
	@JoinColumn(name = "inventory_voucher_header_id")
	private InventoryVoucherHeader inventoryVoucherHeader;

	@NotNull
	@ManyToOne
	private ProductProfile product;

	@Column(name = "quantity")
	private double quantity;

	@Column(name = "updated_quantity", nullable = false, columnDefinition = "int default 0")
	private double updatedQuantity;

	@Column(name = "free_quantity")
	private double freeQuantity;

	@Column(name = "selling_rate")
	private double sellingRate;
	
	@Column(name = "updated_selling_rate", columnDefinition = "double precision DEFAULT 0" )
	private double updatedsellingRate;

	@Column(name = "mrp")
	private double mrp;

	@Column(name = "purchase_rate")
	private BigDecimal purchaseRate;

	@Column(name = "tax_percentage")
	private double taxPercentage;

	@Column(name = "discount_percentage")
	private double discountPercentage;

	@Column(name = "batch_number")
	private String batchNumber;

	@Column(name = "batch_date")
	private LocalDateTime batchDate;

	@Column(name = "row_total")
	private double rowTotal;

	@Column(name = "updated_row_total", nullable = false, columnDefinition = "int default 0")
	private double updatedRowTotal;

	@Column(name = "discount_amount")
	private double discountAmount;

	@Column(name = "tax_amount")
	private double taxAmount;

	@Column(name = "length")
	private String length;

	@Column(name = "width")
	private String width;

	@Column(name = "thickness")
	private String thickness;

	@Column(name = "size")
	private String size;

	@Column(name = "color")
	private String color;

	@Column(name = "itemtype")
	private String itemtype;

	@ManyToOne
	@JoinColumn(name = "source_stock_location_id", nullable = true)
	private StockLocation sourceStockLocation;

	@ManyToOne
	@JoinColumn(name = "destination_stock_location_id", nullable = true)
	private StockLocation destinationStockLocation;

	// @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@ManyToOne
	@JoinColumn(name = "rference_inventory_voucher_header", nullable = true)
	private InventoryVoucherHeader rferenceInventoryVoucherHeader;

	@ManyToOne
	@JoinColumn(name = "rference_inventory_voucher_detail", nullable = true)
	private InventoryVoucherDetail rferenceInventoryVoucherDetail;

	@Column(name = "remarks")
	private String remarks;

	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name = "inventory_voucher_detail_id")
	private List<InventoryVoucherBatchDetail> inventoryVoucherBatchDetails;

	@NotNull
	@Column(name = "updated_status", nullable = false, columnDefinition = "boolean DEFAULT 'FALSE'")
	private boolean updatedStatus = false;// whether the inventory voucher detail is updated or not

	@Column(name = "volume", nullable = false, columnDefinition = "double precision DEFAULT 0")
	private double volume;

	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "price_level_id")
	private PriceLevel priceLevel;

	@Column(name = "additional_discount ", columnDefinition = "double precision DEFAULT 0")
	private double additionalDiscount;
	
	@Column(name = "reference_invoice_no")
	private String referenceInvoiceNo;
	
	
	
	

	public InventoryVoucherDetail() {
		super();
		// TODO Auto-generated constructor stub
	}

	public InventoryVoucherDetail(ProductProfile product, double quantity, double freeQuantity, double sellingRate,
			double mrp, BigDecimal purchaseRate, double taxPercentage, double discountPercentage, String batchNumber,
			LocalDateTime batchDate, double rowTotal, double discountAmount, double taxAmount, String length,
			String width, String thickness, String size, String color, String itemtype,
			StockLocation sourceStockLocation, StockLocation destinationStockLocation,
			InventoryVoucherHeader rferenceInventoryVoucherHeader,
			InventoryVoucherDetail rferenceInventoryVoucherDetail, String remarks,
			List<InventoryVoucherBatchDetail> inventoryVoucherBatchDetails,double additionalDiscount,String referenceInvoiceNo ) {
		super();
		this.product = product;
		this.quantity = quantity;
		this.freeQuantity = freeQuantity;
		this.sellingRate = sellingRate;
		this.mrp = mrp;
		this.purchaseRate = purchaseRate;
		this.taxPercentage = taxPercentage;
		this.discountPercentage = discountPercentage;
		this.batchNumber = batchNumber;
		this.batchDate = batchDate;
		this.rowTotal = rowTotal;
		this.discountAmount = discountAmount;
		this.taxAmount = taxAmount;
		this.length = length;
		this.width = width;
		this.thickness = thickness;
		this.size = size;
		this.color = color;
		this.itemtype = itemtype;
		this.sourceStockLocation = sourceStockLocation;
		this.destinationStockLocation = destinationStockLocation;
		this.rferenceInventoryVoucherHeader = rferenceInventoryVoucherHeader;
		this.rferenceInventoryVoucherDetail = rferenceInventoryVoucherDetail;
		this.remarks = remarks;
		this.inventoryVoucherBatchDetails = inventoryVoucherBatchDetails;
		this.volume = product.getUnitQty() != null ? product.getUnitQty() * quantity : quantity * 1;
		this.additionalDiscount=additionalDiscount;
		this.referenceInvoiceNo=referenceInvoiceNo;
	}

	public InventoryVoucherDetail(ProductProfile product, double quantity, double freeQuantity, double sellingRate,
			double mrp, BigDecimal purchaseRate, double taxPercentage, double discountPercentage, String batchNumber,
			LocalDateTime batchDate, double rowTotal, double discountAmount, double taxAmount, String length,
			String width, String thickness, String size, String color, String itemtype,
			StockLocation sourceStockLocation, StockLocation destinationStockLocation,
			InventoryVoucherHeader rferenceInventoryVoucherHeader,
			InventoryVoucherDetail rferenceInventoryVoucherDetail, String remarks,
			List<InventoryVoucherBatchDetail> inventoryVoucherBatchDetails,
			PriceLevel inventoryVoucherDetailPriceLevel,double additionalDiscount,String referenceInvoiceNo ) {
		super();
		this.product = product;
		this.quantity = quantity;
		this.freeQuantity = freeQuantity;
		this.sellingRate = sellingRate;
		this.mrp = mrp;
		this.purchaseRate = purchaseRate;
		this.taxPercentage = taxPercentage;
		this.discountPercentage = discountPercentage;
		this.batchNumber = batchNumber;
		this.batchDate = batchDate;
		this.rowTotal = rowTotal;
		this.discountAmount = discountAmount;
		this.taxAmount = taxAmount;
		this.length = length;
		this.width = width;
		this.thickness = thickness;
		this.size = size;
		this.color = color;
		this.itemtype = itemtype;
		this.sourceStockLocation = sourceStockLocation;
		this.destinationStockLocation = destinationStockLocation;
		this.rferenceInventoryVoucherHeader = rferenceInventoryVoucherHeader;
		this.rferenceInventoryVoucherDetail = rferenceInventoryVoucherDetail;
		this.remarks = remarks;
		this.inventoryVoucherBatchDetails = inventoryVoucherBatchDetails;
		this.volume = product.getUnitQty() != null ? product.getUnitQty() * quantity : quantity * 1;
		this.priceLevel = inventoryVoucherDetailPriceLevel;
		this.additionalDiscount=additionalDiscount;
		this.referenceInvoiceNo = referenceInvoiceNo;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public InventoryVoucherHeader getInventoryVoucherHeader() {
		return inventoryVoucherHeader;
	}

	public void setInventoryVoucherHeader(InventoryVoucherHeader inventoryVoucherHeader) {
		this.inventoryVoucherHeader = inventoryVoucherHeader;
	}

	public ProductProfile getProduct() {
		return product;
	}

	public void setProduct(ProductProfile product) {
		this.product = product;
	}

	public double getQuantity() {
		return quantity;
	}

	public void setQuantity(double quantity) {
		this.quantity = quantity;
	}

	public double getUpdatedQuantity() {
		return updatedQuantity;
	}

	public void setUpdatedQuantity(double updatedQuantity) {
		this.updatedQuantity = updatedQuantity;
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

	public double getMrp() {
		return mrp;
	}

	public void setMrp(double mrp) {
		this.mrp = mrp;
	}

	public BigDecimal getPurchaseRate() {
		return purchaseRate;
	}

	public void setPurchaseRate(BigDecimal purchaseRate) {
		this.purchaseRate = purchaseRate;
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

	public String getBatchNumber() {
		return batchNumber;
	}

	public void setBatchNumber(String batchNumber) {
		this.batchNumber = batchNumber;
	}

	public LocalDateTime getBatchDate() {
		return batchDate;
	}

	public void setBatchDate(LocalDateTime batchDate) {
		this.batchDate = batchDate;
	}

	public double getRowTotal() {
		return rowTotal;
	}

	public void setRowTotal(double rowTotal) {
		this.rowTotal = rowTotal;
	}

	public double getUpdatedRowTotal() {
		return updatedRowTotal;
	}

	public void setUpdatedRowTotal(double updatedRowTotal) {
		this.updatedRowTotal = updatedRowTotal;
	}

	public double getDiscountAmount() {
		return discountAmount;
	}

	public void setDiscountAmount(double discountAmount) {
		this.discountAmount = discountAmount;
	}

	public double getTaxAmount() {
		return taxAmount;
	}

	public void setTaxAmount(double taxAmount) {
		this.taxAmount = taxAmount;
	}

	public String getLength() {
		return length;
	}

	public void setLength(String length) {
		this.length = length;
	}

	public String getWidth() {
		return width;
	}

	public void setWidth(String width) {
		this.width = width;
	}

	public String getThickness() {
		return thickness;
	}

	public void setThickness(String thickness) {
		this.thickness = thickness;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public StockLocation getSourceStockLocation() {
		return sourceStockLocation;
	}

	public void setSourceStockLocation(StockLocation sourceStockLocation) {
		this.sourceStockLocation = sourceStockLocation;
	}

	public StockLocation getDestinationStockLocation() {
		return destinationStockLocation;
	}

	public void setDestinationStockLocation(StockLocation destinationStockLocation) {
		this.destinationStockLocation = destinationStockLocation;
	}

	public InventoryVoucherHeader getRferenceInventoryVoucherHeader() {
		return rferenceInventoryVoucherHeader;
	}

	public void setRferenceInventoryVoucherHeader(InventoryVoucherHeader rferenceInventoryVoucherHeader) {
		this.rferenceInventoryVoucherHeader = rferenceInventoryVoucherHeader;
	}

	public InventoryVoucherDetail getRferenceInventoryVoucherDetail() {
		return rferenceInventoryVoucherDetail;
	}

	public void setRferenceInventoryVoucherDetail(InventoryVoucherDetail rferenceInventoryVoucherDetail) {
		this.rferenceInventoryVoucherDetail = rferenceInventoryVoucherDetail;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public List<InventoryVoucherBatchDetail> getInventoryVoucherBatchDetails() {
		return inventoryVoucherBatchDetails;
	}

	public void setInventoryVoucherBatchDetails(List<InventoryVoucherBatchDetail> inventoryVoucherBatchDetails) {
		this.inventoryVoucherBatchDetails = inventoryVoucherBatchDetails;
	}

	public boolean getUpdatedStatus() {
		return updatedStatus;
	}

	public void setUpdatedStatus(boolean updatedStatus) {
		this.updatedStatus = updatedStatus;
	}

	public String getItemtype() {
		return itemtype;
	}

	public void setItemtype(String itemtype) {
		this.itemtype = itemtype;
	}

	public double getVolume() {
		return volume;
	}

	public void setVolume(double volume) {
		this.volume = volume;
	}

	public PriceLevel getPriceLevel() {
		return priceLevel;
	}

	public void setPriceLevel(PriceLevel priceLevel) {
		this.priceLevel = priceLevel;
	}

	public Double getAdditionalDiscount() {
		return additionalDiscount;
	}

	public void setAdditionalDiscount(Double additionalDiscount) {
		this.additionalDiscount = additionalDiscount;
	}

	public double getUpdatedsellingRate() {
		return updatedsellingRate;
	}

	public void setUpdatedsellingRate(double updatedsellingRate) {
		this.updatedsellingRate = updatedsellingRate;
	}

	public String getReferenceInvoiceNo() {
		return referenceInvoiceNo;
	}

	public void setReferenceInvoiceNo(String referenceInvoiceNo) {
		this.referenceInvoiceNo = referenceInvoiceNo;
	}



	
}

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

	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name = "inventory_voucher_header_id")
	private InventoryVoucherHeader inventoryVoucherHeader;

	@NotNull
	@ManyToOne
	private ProductProfile product;

	@Column(name = "quantity")
	private double quantity;

	@Column(name = "free_quantity")
	private double freeQuantity;

	@Column(name = "selling_rate")
	private double sellingRate;

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

	@ManyToOne
	@JoinColumn(name = "source_stock_location_id", nullable = true)
	private StockLocation sourceStockLocation;

	@ManyToOne
	@JoinColumn(name = "destination_stock_location_id", nullable = true)
	private StockLocation destinationStockLocation;

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

	public InventoryVoucherDetail() {
	}

	public InventoryVoucherDetail(ProductProfile product, double quantity, double freeQuantity, double sellingRate,
			double mrp, BigDecimal purchaseRate, double taxPercentage, double discountPercentage, String batchNumber,
			LocalDateTime batchDate, double rowTotal, double discountAmount, double taxAmount, String length,
			String width, String thickness, String size, String color, StockLocation sourceStockLocation,
			StockLocation destinationStockLocation, InventoryVoucherHeader rferenceInventoryVoucherHeader,
			InventoryVoucherDetail rferenceInventoryVoucherDetail, String remarks,
			List<InventoryVoucherBatchDetail> inventoryVoucherBatchDetails) {
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
		this.sourceStockLocation = sourceStockLocation;
		this.destinationStockLocation = destinationStockLocation;
		this.rferenceInventoryVoucherHeader = rferenceInventoryVoucherHeader;
		this.rferenceInventoryVoucherDetail = rferenceInventoryVoucherDetail;
		this.remarks = remarks;
		this.inventoryVoucherBatchDetails = inventoryVoucherBatchDetails;
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

	
	

}

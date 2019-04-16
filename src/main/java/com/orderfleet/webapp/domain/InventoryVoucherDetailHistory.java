package com.orderfleet.webapp.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

/**
 * A InventoryVoucherDetailHistory.
 * 
 * @author Muhammed Riyas T
 * @since October 24, 2016
 */
@Entity
@Table(name = "tbl_inventory_voucher_detail_history")
public class InventoryVoucherDetailHistory implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GenericGenerator(name = "seq_inventory_voucher_detail_history_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "seq_inventory_voucher_detail_history_id") })
	@GeneratedValue(generator = "seq_inventory_voucher_detail_history_id_GEN")
	@Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_inventory_voucher_detail_history_id')")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "inventory_voucher_header_history_id")
	private InventoryVoucherHeaderHistory inventoryVoucherHeaderHistory;

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

	public InventoryVoucherDetailHistory() {
	}

	public InventoryVoucherDetailHistory(InventoryVoucherDetail inventoryVoucherDetail) {
		super();
		this.product = inventoryVoucherDetail.getProduct();
		this.quantity = inventoryVoucherDetail.getQuantity();
		this.freeQuantity = inventoryVoucherDetail.getFreeQuantity();
		this.sellingRate = inventoryVoucherDetail.getSellingRate();
		this.mrp = inventoryVoucherDetail.getMrp();
		this.purchaseRate = inventoryVoucherDetail.getPurchaseRate();
		this.taxPercentage = inventoryVoucherDetail.getTaxPercentage();
		this.discountPercentage = inventoryVoucherDetail.getDiscountPercentage();
		this.batchNumber = inventoryVoucherDetail.getBatchNumber();
		this.batchDate = inventoryVoucherDetail.getBatchDate();
		this.rowTotal = inventoryVoucherDetail.getRowTotal();
		this.discountAmount = inventoryVoucherDetail.getDiscountAmount();
		this.taxAmount = inventoryVoucherDetail.getTaxAmount();
		this.length = inventoryVoucherDetail.getLength();
		this.width = inventoryVoucherDetail.getWidth();
		this.thickness = inventoryVoucherDetail.getThickness();
		this.size = inventoryVoucherDetail.getSize();
		this.color = inventoryVoucherDetail.getColor();
		this.sourceStockLocation = inventoryVoucherDetail.getSourceStockLocation();
		this.destinationStockLocation = inventoryVoucherDetail.getDestinationStockLocation();
		this.rferenceInventoryVoucherHeader = inventoryVoucherDetail.getRferenceInventoryVoucherHeader();
		this.rferenceInventoryVoucherDetail = inventoryVoucherDetail.getRferenceInventoryVoucherDetail();
		this.remarks = inventoryVoucherDetail.getRemarks();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public InventoryVoucherHeaderHistory getInventoryVoucherHeaderHistory() {
		return inventoryVoucherHeaderHistory;
	}

	public void setInventoryVoucherHeaderHistory(InventoryVoucherHeaderHistory inventoryVoucherHeaderHistory) {
		this.inventoryVoucherHeaderHistory = inventoryVoucherHeaderHistory;
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

	@Override
	public String toString() {
		return "InventoryVoucherDetail [id=" + id + ", product=" + product + ", quantity=" + quantity
				+ ", freeQuantity=" + freeQuantity + ", sellingRate=" + sellingRate + ", mrp=" + mrp + ", purchaseRate="
				+ purchaseRate + ", taxPercentage=" + taxPercentage + ", discountPercentage=" + discountPercentage
				+ ", batchNumber=" + batchNumber + ", batchDate=" + batchDate + ", rowTotal=" + rowTotal
				+ ", discountAmount=" + discountAmount + ", taxAmount=" + taxAmount + ", length=" + length + ", width="
				+ width + ", thickness=" + thickness + ", size=" + size + "]";
	}

}

package com.orderfleet.webapp.domain;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

/**
 * A Inventory Voucher Batch Detail
 *
 * @author Sarath
 * @since Dec 7, 2016
 */

@Entity
@Table(name = "tbl_inventory_voucher_batch_detail")
public class InventoryVoucherBatchDetail implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GenericGenerator(name = "seq_inventory_voucher_batch_detail_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "seq_inventory_voucher_batch_detail_id") })
	@GeneratedValue(generator = "seq_inventory_voucher_batch_detail_id_GEN")
	@Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_inventory_voucher_batch_detail_id')")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private InventoryVoucherDetail inventoryVoucherDetail;

	@NotNull
	@ManyToOne
	private ProductProfile productProfile;

	@ManyToOne
	private StockLocation stockLocation;

	@Column(name = "batch_number")
	private String batchNumber;

	@Column(name = "batch_date")
	private LocalDateTime batchDate;

	@Column(name = "remarks")
	private String remarks;

	@Column(name = "quantity")
	private double quantity;

	@NotNull
	@ManyToOne
	private Company company;

	public InventoryVoucherBatchDetail() {
		super();
	}

	public InventoryVoucherBatchDetail(Long id, InventoryVoucherDetail inventoryVoucherDetail,
			ProductProfile productProfile, String batchNumber, LocalDateTime batchDate, String remarks, double quantity,
			Company company) {
		super();
		this.id = id;
		this.inventoryVoucherDetail = inventoryVoucherDetail;
		this.productProfile = productProfile;
		this.batchNumber = batchNumber;
		this.batchDate = batchDate;
		this.remarks = remarks;
		this.quantity = quantity;
		this.company = company;
	}

	public InventoryVoucherBatchDetail(ProductProfile productProfile, String batchNumber, LocalDateTime batchDate,
			String remarks, double quantity, Company company, StockLocation stockLocation) {
		super();
		this.productProfile = productProfile;
		this.batchNumber = batchNumber;
		this.batchDate = batchDate;
		this.remarks = remarks;
		this.quantity = quantity;
		this.company = company;
		this.stockLocation = stockLocation;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public InventoryVoucherDetail getInventoryVoucherDetail() {
		return inventoryVoucherDetail;
	}

	public void setInventoryVoucherDetail(InventoryVoucherDetail inventoryVoucherDetail) {
		this.inventoryVoucherDetail = inventoryVoucherDetail;
	}

	public ProductProfile getProductProfile() {
		return productProfile;
	}

	public void setProductProfile(ProductProfile productProfile) {
		this.productProfile = productProfile;
	}

	public StockLocation getStockLocation() {
		return stockLocation;
	}

	public void setStockLocation(StockLocation stockLocation) {
		this.stockLocation = stockLocation;
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

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public double getQuantity() {
		return quantity;
	}

	public void setQuantity(double quantity) {
		this.quantity = quantity;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	@Override
	public String toString() {
		return "InventoryVoucherBatchDetail [id=" + id + ", inventoryVoucherDetail=" + inventoryVoucherDetail
				+ ", productProfile=" + productProfile + ", batchNumber=" + batchNumber + ", batchDate=" + batchDate
				+ ", remarks=" + remarks + ", quantity=" + quantity + ", company=" + company + "]";
	}

}

package com.orderfleet.webapp.domain;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * A OpeningStock.
 * 
 * @author Muhammed Riyas T
 * @since July 15, 2016
 */
@Entity
@Table(name = "tbl_temporary_opening_stock")
public class TemporaryOpeningStock implements Serializable {

	private static final long serialVersionUID = -7628249532116947037L;
	@Id
	@GenericGenerator(name = "seq_temporary_opening_stock_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "seq_temporary_opening_stock_id") })
	@GeneratedValue(generator = "seq_temporary_opening_stock_id_GEN")
	@Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_temporary_opening_stock_id')")
	private Long id;

	@NotNull
	@Column(name = "pid", unique = true, nullable = false, updatable = false)
	private String pid;

	@ManyToOne
	@NotNull
	private ProductProfile productProfile;

	@Column(name = "batch_number")
	private String batchNumber;

	@ManyToOne
	@NotNull
	private StockLocation stockLocation;

	@Column(name = "quantity")
	private double quantity;

	@NotNull
	@Column(name = "created_date", nullable = false)
	private LocalDateTime createdDate;

	@NotNull
	@Column(name = "planned_date", nullable = false)
	private LocalDateTime openingStockDate;

	@ManyToOne
	@NotNull
	private Company company;

	@NotNull
	@Column(name = "activated", nullable = false, columnDefinition = "boolean DEFAULT 'TRUE'")
	private boolean activated = true;

	@Column(name = "last_modified_date", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	@JsonIgnore
	private LocalDateTime lastModifiedDate = LocalDateTime.now();

	@PreUpdate
	public void preUpdate() {
		this.lastModifiedDate = LocalDateTime.now();
	}

	public boolean getActivated() {
		return activated;
	}

	public void setActivated(boolean activated) {
		this.activated = activated;
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

	public ProductProfile getProductProfile() {
		return productProfile;
	}

	public void setProductProfile(ProductProfile productProfile) {
		this.productProfile = productProfile;
	}

	public String getBatchNumber() {
		return batchNumber;
	}

	public void setBatchNumber(String batchNumber) {
		this.batchNumber = batchNumber;
	}

	public StockLocation getStockLocation() {
		return stockLocation;
	}

	public void setStockLocation(StockLocation stockLocation) {
		this.stockLocation = stockLocation;
	}

	public double getQuantity() {
		return quantity;
	}

	public void setQuantity(double quantity) {
		this.quantity = quantity;
	}

	public LocalDateTime getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(LocalDateTime createdDate) {
		this.createdDate = createdDate;
	}

	public LocalDateTime getOpeningStockDate() {
		return openingStockDate;
	}

	public void setOpeningStockDate(LocalDateTime openingStockDate) {
		this.openingStockDate = openingStockDate;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public LocalDateTime getLastModifiedDate() {
		return lastModifiedDate;
	}

	@Override
	public String toString() {
		return "OpeningStock [id=" + id + ", pid=" + pid + ", productProfile=" + productProfile + ", batchNumber="
				+ batchNumber + ", stockLocation=" + stockLocation + ", quantity=" + quantity + ", createdDate="
				+ createdDate + ", openingStockDate=" + openingStockDate + ", company=" + company + ", activated="
				+ activated + "]";
	}
}

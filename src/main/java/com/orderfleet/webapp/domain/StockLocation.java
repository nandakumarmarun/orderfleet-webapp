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
import javax.persistence.ManyToOne;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.orderfleet.webapp.domain.enums.StockLocationType;

/**
 * A StockLocation.
 * 
 * @author Muhammed Riyas T
 * @since July 16, 2016
 */
@Entity
@Table(name = "tbl_stock_location")
public class StockLocation implements Serializable, Cloneable {

	private static final long serialVersionUID = 1L;

	@Id
	@GenericGenerator(name = "seq_stock_location_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "seq_stock_location_id") })
	@GeneratedValue(generator = "seq_stock_location_id_GEN")
	@Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_stock_location_id')")
	private Long id;

	@NotNull
	@Column(name = "pid", unique = true, nullable = false, updatable = false)
	private String pid;

	@NotNull
	@Size(min = 1, max = 255)
	@Column(name = "name", length = 255, nullable = false)
	private String name;

	@Size(max = 55)
	@Column(name = "alias", length = 55)
	private String alias;

	@Column(name = "description")
	private String description;
	
	@Column(name = "stock_location_id")
	private String stockLocationId;
	
	@Column(name = "stock_location_code")
	private String stockLocationCode;

	@ManyToOne
	@NotNull
	private Company company;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "stock_location_type", nullable = false, columnDefinition = "character varying DEFAULT 'ACTUAL'")
	private StockLocationType stockLocationType;

	@NotNull
	@Column(name = "activated", nullable = false, columnDefinition = "boolean DEFAULT 'TRUE'")
	private boolean activated = true;

	@Column(name = "created_date", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	@JsonIgnore
	private LocalDateTime createdDate = LocalDateTime.now();

	@Column(name = "last_modified_date" , nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
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
	

	public String getStockLocationId() {
		return stockLocationId;
	}

	public void setStockLocationId(String stockLocationId) {
		this.stockLocationId = stockLocationId;
	}

	public String getStockLocationCode() {
		return stockLocationCode;
	}

	public void setStockLocationCode(String stockLocationCode) {
		this.stockLocationCode = stockLocationCode;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public StockLocationType getStockLocationType() {
		return stockLocationType;
	}

	public void setStockLocationType(StockLocationType stockLocationType) {
		this.stockLocationType = stockLocationType;
	}

	public LocalDateTime getCreatedDate() {
		return createdDate;
	}

	public LocalDateTime getLastModifiedDate() {
		return lastModifiedDate;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		StockLocation accountType = (StockLocation) o;
		if (accountType.id == null || id == null) {
			return false;
		}
		return Objects.equals(id, accountType.id);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(id);
	}

	
	

	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	@Override
	public String toString() {
		return "StockLocation [id=" + id + ", pid=" + pid + ", name=" + name + ", alias=" + alias + ", description="
				+ description + ", stockLocationId=" + stockLocationId + ", stockLocationCode=" + stockLocationCode
				+ ", company=" + company + ", stockLocationType=" + stockLocationType + ", activated=" + activated
				+ "]";
	}
}

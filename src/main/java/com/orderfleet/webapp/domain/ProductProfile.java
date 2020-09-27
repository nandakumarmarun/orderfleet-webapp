package com.orderfleet.webapp.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.orderfleet.webapp.domain.enums.DataSourceType;
import com.orderfleet.webapp.domain.enums.StockAvailabilityStatus;

/**
 * A ProductProfile.
 *
 * @author Shaheer
 * @since May 17, 2016
 */
@Entity
@Table(name = "tbl_product_profile")
public class ProductProfile implements Serializable, Cloneable {

	private static final long serialVersionUID = 1L;

	@Id
	@GenericGenerator(name = "seq_product_profile_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "seq_product_profile_id") })
	@GeneratedValue(generator = "seq_product_profile_id_GEN")
	@Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_product_profile_id')")
	private Long id;

	@NotNull
	@Column(name = "pid", unique = true, nullable = false, updatable = false)
	private String pid;

	@NotNull
	@Size(max = 255)
	@Column(name = "name", length = 255, nullable = false)
	private String name;

	@Size(max = 255)
	@Column(name = "alias", length = 255)
	private String alias;

	@Column(name = "description")
	private String description;

	@Column(name = "product_id")
	private String productId;

	@NotNull
	@Column(name = "price", precision = 10, scale = 2, nullable = false)
	private BigDecimal price;

	@Column(name = "mrp", columnDefinition = "double precision DEFAULT 0")
	private double mrp;

	@Column(name = "sku")
	private String sku;

	@Column(name = "unit_qty", columnDefinition = "double precision DEFAULT 1")
	private Double unitQty = 1d;

	@Column(name = "tax_rate", columnDefinition = "double precision DEFAULT 0")
	private double taxRate;

	@ManyToOne
	@NotNull
	private Company company;

	@ManyToOne
	@NotNull
	private ProductCategory productCategory;

	@ManyToOne
	@NotNull
	private Division division;

	@Size(max = 50000)
	@Lob
	@Column(name = "color_image")
	private byte[] colorImage;

	@Column(name = "color_image_content_type", length = 255)
	private String colorImageContentType;

	@Column(name = "size", columnDefinition = "character varying DEFAULT ''")
	private String size;

	@JsonIgnore
	@ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinTable(name = "tbl_product_profile_images", joinColumns = {
			@JoinColumn(name = "product_profile_id", referencedColumnName = "id") }, inverseJoinColumns = {
					@JoinColumn(name = "file_id", referencedColumnName = "id") })
	private Set<File> files = new HashSet<>();

	@NotNull
	@Column(name = "activated", nullable = false, columnDefinition = "boolean DEFAULT 'TRUE'")
	private boolean activated = true;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "data_source_type", nullable = false, columnDefinition = "character varying DEFAULT 'WEB'")
	private DataSourceType dataSourceType = DataSourceType.WEB;

	@NotNull
	@Column(name = "thirdparty_update", nullable = false, columnDefinition = "boolean DEFAULT 'true'")
	private boolean thirdpartyUpdate = true;

	@Column(name = "default_ledger", length = 255)
	private String defaultLedger;

	@Column(name = "created_date", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	@JsonIgnore
	private LocalDateTime createdDate = LocalDateTime.now();

	@Column(name = "last_modified_date", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	@JsonIgnore
	private LocalDateTime lastModifiedDate = LocalDateTime.now();

	@JsonIgnore
	@ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinTable(name = "tbl_product_profile_tax_master", joinColumns = {
			@JoinColumn(name = "product_profile_id", referencedColumnName = "id") }, inverseJoinColumns = {
					@JoinColumn(name = "tax_master_id", referencedColumnName = "id") })
	@Fetch(value = FetchMode.SUBSELECT)
	private List<TaxMaster> taxMastersList = new ArrayList<>();

	@PreUpdate
	public void preUpdate() {
		this.lastModifiedDate = LocalDateTime.now();
	}

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "stock_availability_status", nullable = false, columnDefinition = "character varying DEFAULT 'AVAILABLE'")
	private StockAvailabilityStatus stockAvailabilityStatus = StockAvailabilityStatus.AVAILABLE;

	@Column(name = "hsn_code")
	private String hsnCode;

	@Column(name = "trim_char")
	private String trimChar;

	@Column(name = "product_description")
	private String productDescription;

	@Column(name = "barcode")
	private String barcode;

	@Column(name = "remarks")
	private String remarks;

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

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public ProductCategory getProductCategory() {
		return productCategory;
	}

	public void setProductCategory(ProductCategory productCategory) {
		this.productCategory = productCategory;
	}

	public Division getDivision() {
		return division;
	}

	public void setDivision(Division division) {
		this.division = division;
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

	public Set<File> getFiles() {
		return files;
	}

	public void setFiles(Set<File> files) {
		this.files = files;
	}

	public boolean getActivated() {
		return activated;
	}

	public void setActivated(boolean activated) {
		this.activated = activated;
	}

	public DataSourceType getDataSourceType() {
		return dataSourceType;
	}

	public void setDataSourceType(DataSourceType dataSourceType) {
		this.dataSourceType = dataSourceType;
	}

	public boolean getThirdpartyUpdate() {
		return thirdpartyUpdate;
	}

	public void setThirdpartyUpdate(boolean thirdpartyUpdate) {
		this.thirdpartyUpdate = thirdpartyUpdate;
	}

	public LocalDateTime getCreatedDate() {
		return createdDate;
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

	public List<TaxMaster> getTaxMastersList() {
		return taxMastersList;
	}

	public void setTaxMastersList(List<TaxMaster> taxMastersList) {
		this.taxMastersList = taxMastersList;
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

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		ProductProfile productProfile = (ProductProfile) o;
		if (productProfile.id == null || id == null) {
			return false;
		}
		return Objects.equals(id, productProfile.id);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(id);
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

}

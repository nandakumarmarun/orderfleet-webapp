package com.orderfleet.webapp.domain;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

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

/**
 * A ProductGroup.
 * 
 * @author Muhammed Riyas T
 * @since May 17, 2016
 */
@Entity
@Table(name = "tbl_product_group")
public class ProductGroup implements Serializable, Cloneable {

	private static final long serialVersionUID = 1L;

	@Id
	@GenericGenerator(name = "seq_product_group_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "seq_product_group_id") })
	@GeneratedValue(generator = "seq_product_group_id_GEN")
	@Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_product_group_id')")
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

	@Column(name = "product_group_id")
	private String productGroupId;

	@Column(name = "product_group_code")
	private String productGroupCode;

	@ManyToOne
	@NotNull
	private Company company;

	@Lob
	@Column(name = "image")
	private byte[] image;

	@Column(name = "image_content_type", length = 255)
	private String imageContentType;

	@NotNull
	@Column(name = "activated", nullable = false, columnDefinition = "boolean DEFAULT 'TRUE'")
	private boolean activated = true;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "data_source_type", nullable = false, columnDefinition = "character varying DEFAULT 'WEB'")
	private DataSourceType dataSourceType = DataSourceType.WEB;

	@NotNull
	@Column(name = "thirdparty_update", nullable = false, columnDefinition = "boolean DEFAULT 'TRUE'")
	private boolean thirdpartyUpdate = true;

	@Column(name = "created_date", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	@JsonIgnore
	private LocalDateTime createdDate = LocalDateTime.now();

	@Column(name = "last_modified_date", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	@JsonIgnore
	private LocalDateTime lastModifiedDate = LocalDateTime.now();

	@JsonIgnore
	@ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinTable(name = "tbl_product_group_tax_master", joinColumns = {
			@JoinColumn(name = "product_group_id", referencedColumnName = "id") }, inverseJoinColumns = {
					@JoinColumn(name = "tax_master_id", referencedColumnName = "id") })
	@Fetch(value = FetchMode.SUBSELECT)
	private List<TaxMaster> taxMastersList = new ArrayList<>();

	@Column(name = "tax_rate", nullable = false, columnDefinition = "double precision DEFAULT 0")
	private double taxRate;

	@PreUpdate
	public void preUpdate() {
		this.lastModifiedDate = LocalDateTime.now();
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

	public String getProductGroupId() {
		return productGroupId;
	}

	public void setProductGroupId(String productGroupId) {
		this.productGroupId = productGroupId;
	}

	public String getProductGroupCode() {
		return productGroupCode;
	}

	public void setProductGroupCode(String productGroupCode) {
		this.productGroupCode = productGroupCode;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public byte[] getImage() {
		return image;
	}

	public void setImage(byte[] image) {
		this.image = image;
	}

	public String getImageContentType() {
		return imageContentType;
	}

	public void setImageContentType(String imageContentType) {
		this.imageContentType = imageContentType;
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

	public List<TaxMaster> getTaxMastersList() {
		return taxMastersList;
	}

	public void setTaxMastersList(List<TaxMaster> taxMastersList) {
		this.taxMastersList = taxMastersList;
	}

	public double getTaxRate() {
		return taxRate;
	}

	public void setTaxRate(double taxRate) {
		this.taxRate = taxRate;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		ProductGroup productGroup = (ProductGroup) o;
		if (productGroup.id == null || id == null) {
			return false;
		}
		return Objects.equals(id, productGroup.id);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(id);
	}

	@Override
	public String toString() {
		return "ProductGroup [id=" + id + ", pid=" + pid + ", name=" + name + ", alias=" + alias + ", description="
				+ description + ", productGroupId=" + productGroupId + ", productGroupCode=" + productGroupCode
				+ ", company=" + company + ", image=" + Arrays.toString(image) + ", imageContentType="
				+ imageContentType + ", activated=" + activated + ", dataSourceType=" + dataSourceType
				+ ", thirdpartyUpdate=" + thirdpartyUpdate + ", createdDate=" + createdDate + ", lastModifiedDate="
				+ lastModifiedDate + ", taxMastersList=" + taxMastersList + ", taxRate=" + taxRate + "]";
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
}

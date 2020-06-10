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
 * A ProductGroupProduct
 * 
 * @author Anish
 * @since June 10, 2020
 */
@Entity
@Table(name = "tbl_ecom_product_group_product")
public class EcomProductGroupProduct implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GenericGenerator(name = "seq_ecom_product_group_product_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "seq_ecom_product_group_product_id") })
	@GeneratedValue(generator = "seq_ecom_product_group_product_id_GEN")
	@Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_ecom_product_group_product_id')")
	private Long id;

	@NotNull
	@ManyToOne
	private ProductProfile product;

	@NotNull
	@ManyToOne
	private EcomProductGroup productGroup;

	@NotNull
	@ManyToOne
	private Company company;

	@Column(name = "created_date", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	@JsonIgnore
	private LocalDateTime createdDate = LocalDateTime.now();

	@Column(name = "last_modified_date", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	@JsonIgnore
	private LocalDateTime lastModifiedDate = LocalDateTime.now();

	@PreUpdate
	public void preUpdate() {
		this.lastModifiedDate = LocalDateTime.now();
	}

	@NotNull
	@Column(name = "activated", nullable = false, columnDefinition = "boolean DEFAULT 'TRUE'")
	private boolean activated = true;

	public EcomProductGroupProduct() {
		super();
	}

	public EcomProductGroupProduct(ProductProfile product, EcomProductGroup productGroup, Company company) {
		super();
		this.product = product;
		this.productGroup = productGroup;
		this.company = company;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public ProductProfile getProduct() {
		return product;
	}

	public void setProduct(ProductProfile product) {
		this.product = product;
	}

	public EcomProductGroup getProductGroup() {
		return productGroup;
	}

	public void setProductGroup(EcomProductGroup productGroup) {
		this.productGroup = productGroup;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public LocalDateTime getCreatedDate() {
		return createdDate;
	}

	public LocalDateTime getLastModifiedDate() {
		return lastModifiedDate;
	}

	public boolean getActivated() {
		return activated;
	}

	public void setActivated(boolean activated) {
		this.activated = activated;
	}

	@Override
	public String toString() {
		return "ProductGroupProduct [id=" + id + ", product=" + product + ", productGroup=" + productGroup + "]";
	}

}
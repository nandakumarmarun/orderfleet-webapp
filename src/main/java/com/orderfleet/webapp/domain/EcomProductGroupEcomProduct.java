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
 * A EcomProductGroupEcomProducts
 * 
 * @author Anish
 * @since June 1, 2020
 */
@Entity
@Table(name = "tbl_ecom_product_group_ecom_products")
public class EcomProductGroupEcomProduct implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GenericGenerator(name = "seq_ecom_product_group_ecom_products_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "seq_ecom_product_group_ecom_products_id") })
	@GeneratedValue(generator = "seq_ecom_product_group_ecom_products_id_GEN")
	@Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_ecom_product_group_ecom_products_id')")
	private Long id;

	@NotNull
	@ManyToOne
	private EcomProductProfile ecomProduct;

	@NotNull
	@ManyToOne
	private EcomProductGroup ecomProductGroup;

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

	public EcomProductGroupEcomProduct() {
		super();
	}

	public EcomProductGroupEcomProduct(EcomProductProfile ecomProduct, EcomProductGroup ecomProductGroup, Company company) {
		super();
		this.ecomProduct = ecomProduct;
		this.ecomProductGroup = ecomProductGroup;
		this.company = company;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public EcomProductProfile getEcomProduct() {
		return ecomProduct;
	}

	public void setEcomProduct(EcomProductProfile ecomProduct) {
		this.ecomProduct = ecomProduct;
	}

	public EcomProductGroup getEcomProductGroup() {
		return ecomProductGroup;
	}

	public void setEcomProductGroup(EcomProductGroup ecomProductGroup) {
		this.ecomProductGroup = ecomProductGroup;
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

	@Override
	public String toString() {
		return "ProductGroupEcomProduct [id=" + id + ", ecomProduct=" + ecomProduct + ", productGroup=" + ecomProductGroup
				+ "]";
	}

}
package com.orderfleet.webapp.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

/**
 * A Ecom Product Profile Product
 * 
 * @author Sarath
 * @since Sep 23, 2016
 */
@Entity
@Table(name = "tbl_ecom_product_profile_product")
public class EcomProductProfileProduct implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GenericGenerator(name = "seq_ecom_product_profile_product_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "seq_ecom_product_profile_product_id") })
	@GeneratedValue(generator = "seq_ecom_product_profile_product_id_GEN")
	@Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_ecom_product_profile_product_id')")
	private Long id;

	@NotNull
	@ManyToOne
	private ProductProfile product;

	@NotNull
	@ManyToOne
	private EcomProductProfile ecomProductProfile;

	@NotNull
	@ManyToOne
	private Company company;

	public EcomProductProfileProduct() {
		super();
	}

	public EcomProductProfileProduct(ProductProfile product, EcomProductProfile ecomProductProfile, Company company) {
		super();
		this.product = product;
		this.ecomProductProfile = ecomProductProfile;
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

	public EcomProductProfile getEcomProductProfile() {
		return ecomProductProfile;
	}

	public void setEcomProductProfile(EcomProductProfile ecomProductProfile) {
		this.ecomProductProfile = ecomProductProfile;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	@Override
	public String toString() {
		return "EcomProductGroupProduct [id=" + id + ", product=" + product + ", ecomProductGroup=" + ecomProductProfile
				+ "]";
	}

}
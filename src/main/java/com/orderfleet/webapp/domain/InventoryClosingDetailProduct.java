package com.orderfleet.webapp.domain;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

@Entity
@Table(name = "tbl_inventory_closing_detail_product")
public class InventoryClosingDetailProduct implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GenericGenerator(name = "seq_inventory_closing_detail_product_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "seq_inventory_closing_detail_product_id") })
	@GeneratedValue(generator = "seq_inventory_closing_detail_product_id_GEN")
	@Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_inventory_closing_detail_product_id')")
	private Long id;
	
	@NotNull
	private InventoryClosingHeader invntoryClosingHeader;
	
	@NotNull
	private ProductProfile productProfile;
	
	private double initial;
	
	private double closing;

	@NotNull
	private LocalDateTime createdDate = LocalDateTime.now();
	
	@NotNull
	private User createdBy;
	
	@NotNull
	@ManyToOne
	private Company company;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public InventoryClosingHeader getInvntoryClosingHeader() {
		return invntoryClosingHeader;
	}

	public void setInvntoryClosingHeader(InventoryClosingHeader invntoryClosingHeader) {
		this.invntoryClosingHeader = invntoryClosingHeader;
	}

	public ProductProfile getProductProfile() {
		return productProfile;
	}

	public void setProductProfile(ProductProfile productProfile) {
		this.productProfile = productProfile;
	}

	public double getInitial() {
		return initial;
	}

	public void setInitial(double initial) {
		this.initial = initial;
	}

	public double getClosing() {
		return closing;
	}

	public void setClosing(double closing) {
		this.closing = closing;
	}

	public LocalDateTime getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(LocalDateTime createdDate) {
		this.createdDate = createdDate;
	}

	public User getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(User createdBy) {
		this.createdBy = createdBy;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

}

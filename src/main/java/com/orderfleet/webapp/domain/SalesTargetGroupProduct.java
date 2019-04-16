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
 * A SalesTargetGroupProduct
 *
 * @author Sarath
 * @since Oct 14, 2016
 */
@Entity
@Table(name = "tbl_sales_target_group_product")
public class SalesTargetGroupProduct implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GenericGenerator(name = "seq_sales_target_group_product_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "seq_sales_target_group_product_id") })
	@GeneratedValue(generator = "seq_sales_target_group_product_id_GEN")
	@Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_sales_target_group_product_id')")
	private Long id;

	@NotNull
	@ManyToOne
	private ProductProfile product;

	@NotNull
	@ManyToOne
	private SalesTargetGroup salesTargetGroup;

	@NotNull
	@ManyToOne
	private Company company;

	public SalesTargetGroupProduct() {
		super();
	}

	public SalesTargetGroupProduct(ProductProfile product, SalesTargetGroup salesTargetGroup, Company company) {
		super();
		this.product = product;
		this.salesTargetGroup = salesTargetGroup;
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

	public SalesTargetGroup getSalesTargetGroup() {
		return salesTargetGroup;
	}

	public void setSalesTargetGroup(SalesTargetGroup salesTargetGroup) {
		this.salesTargetGroup = salesTargetGroup;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	@Override
	public String toString() {
		return "SalesTargetGroupProduct [id=" + id + ", product=" + product + ", salesTargetGroup=" + salesTargetGroup
				+ ", company=" + company + "]";
	}

}

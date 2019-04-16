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

@Entity
@Table(name = "tbl_product_group_sales_target_group")
public class ProductGroupSalesTargetGroup implements Serializable{
	
	private static final long serialVersionUID = 1L;

	@Id
	@GenericGenerator(name = "seq_product_group_sales_target_group_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "seq_product_group_sales_target_group_id") })
	@GeneratedValue(generator = "seq_product_group_sales_target_group_id_GEN")
	@Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_product_group_sales_target_group_id')")
	private Long id;
	
	@NotNull
	@ManyToOne
	private ProductGroup productGroup;
	
	@NotNull
	@ManyToOne
	private SalesTargetGroup salesTargetGroup;
	
	@NotNull
	@ManyToOne
	private Company company;
	
	public ProductGroupSalesTargetGroup() {
		super();
		// TODO Auto-generated constructor stub
	}

	public ProductGroupSalesTargetGroup(Long id, ProductGroup productGroup, SalesTargetGroup salesTargetGroup,
			Company company) {
		super();
		this.id = id;
		this.productGroup = productGroup;
		this.salesTargetGroup = salesTargetGroup;
		this.company = company;
	}
	
	public ProductGroupSalesTargetGroup(ProductGroup productGroup, SalesTargetGroup salesTargetGroup,
			Company company) {
		super();
		this.productGroup = productGroup;
		this.salesTargetGroup = salesTargetGroup;
		this.company = company;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public ProductGroup getProductGroup() {
		return productGroup;
	}

	public void setProductGroup(ProductGroup productGroup) {
		this.productGroup = productGroup;
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
		return "ProductGroupSalesTargetGroup [id=" + id + ", productGroup=" + productGroup + ", salesTargetGroup="
				+ salesTargetGroup + ", company=" + company + "]";
	}
	
	

}

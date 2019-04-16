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
 * A SalesTargetBlockSalesTargetGroup
 *
 * @author Sarath
 * @since Feb 22, 2017
 */

@Entity
@Table(name = "tbl_sale_target_block_sales_target_group")
public class SalesTargetBlockSalesTargetGroup implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GenericGenerator(name = "seq_sale_target_block_sales_target_group_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "seq_sale_target_block_sales_target_group_id") })
	@GeneratedValue(generator = "seq_sale_target_block_sales_target_group_id_GEN")
	@Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_sale_target_block_sales_target_group_id')")
	private Long id;

	@NotNull
	@ManyToOne
	private SalesTargetBlock salesTargetBlock;

	@NotNull
	@ManyToOne
	private SalesTargetGroup salesTargetGroup;

	@ManyToOne()
	@NotNull
	private Company company;

	public SalesTargetBlockSalesTargetGroup() {
		super();
	}

	public SalesTargetBlockSalesTargetGroup(SalesTargetBlock salesTargetBlock,SalesTargetGroup salesTargetGroup, Company company) {
		super();
		this.salesTargetBlock = salesTargetBlock;
		this.salesTargetGroup = salesTargetGroup;
		this.company = company;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public SalesTargetBlock getSalesTargetBlock() {
		return salesTargetBlock;
	}

	public void setSalesTargetBlock(SalesTargetBlock salesTargetBlock) {
		this.salesTargetBlock = salesTargetBlock;
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

}

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
@Table(name = "tbl_salestarget_reportsetting_salestarget_block")
public class SalesTargetReportSettingSalesTargetBlock implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GenericGenerator(name = "seq_salestarget_reportsetting_salestarget_block_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "seq_salestarget_reportsetting_salestarget_block_id") })
	@GeneratedValue(generator = "seq_salestarget_reportsetting_salestarget_block_id_GEN")
	@Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_salestarget_reportsetting_salestarget_block_id')")
	private Long id;

	@NotNull
	@ManyToOne
	private SalesTargetReportSetting salesTargetReportSetting;

	@NotNull
	@ManyToOne
	private SalesTargetBlock salesTargetBlock;

	@NotNull
	@Column(name = "sort_order", nullable = false)
	private int sortOrder = 0;

	@ManyToOne
	@NotNull
	private Company company;

	public SalesTargetReportSettingSalesTargetBlock() {
		super();
	}

	public SalesTargetReportSettingSalesTargetBlock(SalesTargetReportSetting salesTargetReportSetting,
			SalesTargetBlock salesTargetBlock, int sortOrder) {
		super();
		this.salesTargetReportSetting = salesTargetReportSetting;
		this.salesTargetBlock = salesTargetBlock;
		this.sortOrder = sortOrder;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public SalesTargetReportSetting getSalesTargetReportSetting() {
		return salesTargetReportSetting;
	}

	public void setSalesTargetReportSetting(SalesTargetReportSetting salesTargetReportSetting) {
		this.salesTargetReportSetting = salesTargetReportSetting;
	}

	public SalesTargetBlock getSalesTargetBlock() {
		return salesTargetBlock;
	}

	public void setSalesTargetBlock(SalesTargetBlock salesTargetBlock) {
		this.salesTargetBlock = salesTargetBlock;
	}

	public int getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(int sortOrder) {
		this.sortOrder = sortOrder;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

}

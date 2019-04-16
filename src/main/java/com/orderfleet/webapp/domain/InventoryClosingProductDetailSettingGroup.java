package com.orderfleet.webapp.domain;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import com.orderfleet.webapp.domain.enums.Flow;

@Entity
@Table(name = "tbl_inventory_closing_product_detail_setting_group")
public class InventoryClosingProductDetailSettingGroup implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GenericGenerator(name = "seq_inventory_closing_product_detail_setting_group_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "seq_inventory_closing_product_detail_setting_group_id") })
	@GeneratedValue(generator = "seq_inventory_closing_product_detail_setting_group_id_GEN")
	@Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_inventory_closing_product_detail_setting_group_id')")
	private Long id;
	
	@NotNull
	private InventoryClosingDetailProduct inventoryClosingDetailProduct;
	
	@NotNull
	private InventoryClosingReportSettingGroup inventoryClosingReportSettingGroup;
	
	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "flow", nullable = false)
	private Flow flow;

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

	public InventoryClosingDetailProduct getInventoryClosingDetailProduct() {
		return inventoryClosingDetailProduct;
	}

	public void setInventoryClosingDetailProduct(InventoryClosingDetailProduct inventoryClosingDetailProduct) {
		this.inventoryClosingDetailProduct = inventoryClosingDetailProduct;
	}

	public InventoryClosingReportSettingGroup getInventoryClosingReportSettingGroup() {
		return inventoryClosingReportSettingGroup;
	}

	public void setInventoryClosingReportSettingGroup(
			InventoryClosingReportSettingGroup inventoryClosingReportSettingGroup) {
		this.inventoryClosingReportSettingGroup = inventoryClosingReportSettingGroup;
	}

	public Flow getFlow() {
		return flow;
	}

	public void setFlow(Flow flow) {
		this.flow = flow;
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

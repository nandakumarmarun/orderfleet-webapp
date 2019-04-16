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
@Table(name = "tbl_inventory_closing_report_settings")
public class InventoryClosingReportSettings implements Serializable{

	private static final long serialVersionUID = 1L;

	@Id
	@GenericGenerator(name = "seq_inventory_closing_report_settings_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "seq_inventory_closing_report_settings_id") })
	@GeneratedValue(generator = "seq_inventory_closing_report_settings_id_GEN")
	@Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_inventory_closing_report_settings_id')")
	private Long id;
	
	@NotNull
	@ManyToOne
	private Company company;
	
	@NotNull
	@ManyToOne
	private Document document;
	
	@NotNull
	@ManyToOne
	private InventoryClosingReportSettingGroup inventoryClosingReportSettingGroup;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public Document getDocument() {
		return document;
	}

	public void setDocument(Document document) {
		this.document = document;
	}

	public InventoryClosingReportSettingGroup getInventoryClosingReportSettingGroup() {
		return inventoryClosingReportSettingGroup;
	}

	public void setInventoryClosingReportSettingGroup(
			InventoryClosingReportSettingGroup inventoryClosingReportSettingGroup) {
		this.inventoryClosingReportSettingGroup = inventoryClosingReportSettingGroup;
	}
	

}

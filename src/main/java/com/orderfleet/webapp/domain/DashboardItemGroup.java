package com.orderfleet.webapp.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

@Entity
@Table(name = "tbl_dashboard_item_group")
public class DashboardItemGroup implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GenericGenerator(name = "seq_dashboard_item_group_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "seq_dashboard_item_group_id") })
	@GeneratedValue(generator = "seq_dashboard_item_group_id_GEN")
	@Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_dashboard_item_group_id')")
	private Long id;

	private String name;

	private int sortOrder;
	
	//dash board header summary tile
	@NotNull
	@Column(name = "appear_in_dashboard_summary", nullable = false, columnDefinition = "boolean DEFAULT 'TRUE'")
	private boolean appearInDashboardSummary = true;
	
	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	private Company company;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(int sortOrder) {
		this.sortOrder = sortOrder;
	}

	public boolean getAppearInDashboardSummary() {
		return appearInDashboardSummary;
	}

	public void setAppearInDashboardSummary(boolean appearInDashboardSummary) {
		this.appearInDashboardSummary = appearInDashboardSummary;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

}

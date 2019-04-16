package com.orderfleet.webapp.domain;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import com.orderfleet.webapp.domain.enums.ChartType;

@Entity
@Table(name = "tbl_dashboard_chart_item")
public class DashboardChartItem implements Serializable{

	private static final long serialVersionUID = 1L;

	@Id
	@GenericGenerator(name = "seq_dashboard_chart_item_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "seq_dashboard_chart_item_id") })
	@GeneratedValue(generator = "seq_dashboard_chart_item_id_GEN")
	@Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_dashboard_chart_item_id')")
	private Long id;

	@NotNull
	@Column(name = "pid", unique = true, nullable = false, updatable = false)
	private String pid;

	@NotNull
	@Size(min = 1, max = 255)
	@Column(name = "name", length = 255, nullable = false)
	private String name;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "chart_type", nullable = false)
	private ChartType chartType;

	@ManyToOne(fetch = FetchType.LAZY)
	@NotNull
	private DashboardItem dashboardItem;

	@ManyToOne(fetch = FetchType.LAZY)
	@NotNull
	private Company company;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ChartType getChartType() {
		return chartType;
	}

	public void setChartType(ChartType chartType) {
		this.chartType = chartType;
	}

	public DashboardItem getDashboardItem() {
		return dashboardItem;
	}

	public void setDashboardItem(DashboardItem dashboardItem) {
		this.dashboardItem = dashboardItem;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		DashboardChartItem accountType = (DashboardChartItem) o;
		if (accountType.id == null || id == null) {
			return false;
		}
		return Objects.equals(id, accountType.id);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(id);
	}

}

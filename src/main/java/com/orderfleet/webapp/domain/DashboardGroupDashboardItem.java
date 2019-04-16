package com.orderfleet.webapp.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

@Entity
@Table(name = "tbl_dashboard_group_dashboard_item")
public class DashboardGroupDashboardItem implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GenericGenerator(name = "seq_dashboard_group_dashboard_item_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "seq_dashboard_group_dashboard_item_id") })
	@GeneratedValue(generator = "seq_dashboard_group_dashboard_item_id_GEN")
	@Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_dashboard_group_dashboard_item_id')")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "funnel_id")
	private DashboardItemGroup dashboardItemGroup;
	
	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	private DashboardItem dashboardItem;

	private Long companyId;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public DashboardItemGroup getDashboardItemGroup() {
		return dashboardItemGroup;
	}

	public DashboardItem getDashboardItem() {
		return dashboardItem;
	}

	public Long getCompanyId() {
		return companyId;
	}

	public void setDashboardItemGroup(DashboardItemGroup dashboardItemGroup) {
		this.dashboardItemGroup = dashboardItemGroup;
	}

	public void setDashboardItem(DashboardItem dashboardItem) {
		this.dashboardItem = dashboardItem;
	}

	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}

	@Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DashboardGroupDashboardItem )) return false;
        return id != null && id.equals(((DashboardGroupDashboardItem) o).id);
    }
	
    @Override
    public int hashCode() {
        return 31;
    }
	
}

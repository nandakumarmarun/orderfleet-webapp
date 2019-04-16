package com.orderfleet.webapp.domain;

import java.io.Serializable;
import java.util.Objects;

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
@Table(name ="tbl_dashboard_item_group_user")
public class DashboardItemGroupUser implements Serializable{

	private static final long serialVersionUID = 1L;

	@Id
	@GenericGenerator(name = "seq_dashboard_item_group_user_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "seq_dashboard_item_group_user_id") })
	@GeneratedValue(generator = "seq_dashboard_item_group_user_id_GEN")
	@Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_dashboard_item_group_user_id')")
	private Long id;
	
	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	private DashboardItemGroup dashboardItemGroup;
	
	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	private User user;
	
	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	private Company company;

	public Long getId() {
		return id;
	}

	public DashboardItemGroup getDashboardItemGroup() {
		return dashboardItemGroup;
	}

	public User getUser() {
		return user;
	}

	public Company getCompany() {
		return company;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setDashboardItemGroup(DashboardItemGroup dashboardItemGroup) {
		this.dashboardItemGroup = dashboardItemGroup;
	}

	public void setUser(User user) {
		this.user = user;
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
		DashboardItemGroupUser dashboardItemUser = (DashboardItemGroupUser) o;
		if (dashboardItemUser.id == null || id == null) {
			return false;
		}
		return Objects.equals(id, dashboardItemUser.id);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(id);
	}
	
}

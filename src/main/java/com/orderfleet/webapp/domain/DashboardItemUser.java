package com.orderfleet.webapp.domain;

import java.io.Serializable;
import java.util.Objects;

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
@Table(name ="tbl_dashboard_item_user")
public class DashboardItemUser implements Serializable{

	private static final long serialVersionUID = 1L;

	@Id
	@GenericGenerator(name = "seq_dashboard_item_user_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "seq_dashboard_item_user_id") })
	@GeneratedValue(generator = "seq_dashboard_item_user_id_GEN")
	@Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_dashboard_item_user_id')")
	private Long id;
	
	@NotNull
	@ManyToOne
	private User user;
	
	@NotNull
	@ManyToOne
	private Company company;
	
	@NotNull
	@ManyToOne
	private DashboardItem dashboardItem;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public DashboardItem getDashboardItem() {
		return dashboardItem;
	}

	public void setDashboardItem(DashboardItem dashboardItem) {
		this.dashboardItem = dashboardItem;
	}
	
	

	@Override
	public String toString() {
		return "DashboardItemUser [id=" + id + ", user=" + user + ", company=" + company + ", dashboardItem="
				+ dashboardItem + "]";
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		DashboardItemUser dashboardItemUser = (DashboardItemUser) o;
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

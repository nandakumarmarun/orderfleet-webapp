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

/**
 * A DashboardActivity.
 *
 * @author Sarath
 * @since Oct 27, 2016
 */

@Entity
@Table(name = "tbl_dashboard_activity")
public class DashboardActivity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GenericGenerator(name = "seq_dashboard_activity_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "seq_dashboard_activity_id") })
	@GeneratedValue(generator = "seq_dashboard_activity_id_GEN")
	@Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_dashboard_activity_id')")
	private Long id;

	@NotNull
	@ManyToOne
	private Activity activity;

	@NotNull
	@ManyToOne
	private Company company;

	public DashboardActivity() {
		super();
	}

	public DashboardActivity(Long id, Activity activity, Company company) {
		super();
		this.id = id;
		this.activity = activity;
		this.company = company;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Activity getActivity() {
		return activity;
	}

	public void setActivity(Activity activity) {
		this.activity = activity;
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
		DashboardActivity dashboardActivity = (DashboardActivity) o;
		if (dashboardActivity.id == null || id == null) {
			return false;
		}
		return Objects.equals(id, dashboardActivity.id);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(id);
	}

	@Override
	public String toString() {
		return "DashboardActivity [id=" + id + ", activity=" + activity + ", company=" + company + "]";
	}

}

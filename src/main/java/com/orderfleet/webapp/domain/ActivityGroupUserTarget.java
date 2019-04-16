package com.orderfleet.webapp.domain;

import java.io.Serializable;
import java.time.LocalDate;
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
 * A ActivityGroupUserTarget.
 */
@Entity
@Table(name = "tbl_activity_group_user_target")
public class ActivityGroupUserTarget implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GenericGenerator(name = "seq_activity_group_user_target_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "seq_activity_group_user_target_id") })
	@GeneratedValue(generator = "seq_activity_group_user_target_id_GEN")
	@Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_activity_group_user_target_id')")
	private Long id;

	@NotNull
	@Column(name = "pid", unique = true, nullable = false, updatable = false)
	private String pid;

	@NotNull
	@Column(name = "start_date", nullable = false)
	private LocalDate startDate;

	@NotNull
	@Column(name = "end_date", nullable = false)
	private LocalDate endDate;

	@Column(name = "target_number")
	private Long targetNumber;

	@ManyToOne
	@NotNull
	private ActivityGroup activityGroup;

	@ManyToOne
	@NotNull
	private User user;

	@ManyToOne
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

	public LocalDate getStartDate() {
		return startDate;
	}

	public void setStartDate(LocalDate startDate) {
		this.startDate = startDate;
	}

	public LocalDate getEndDate() {
		return endDate;
	}

	public void setEndDate(LocalDate endDate) {
		this.endDate = endDate;
	}

	public ActivityGroup getActivityGroup() {
		return activityGroup;
	}

	public void setActivityGroup(ActivityGroup activityGroup) {
		this.activityGroup = activityGroup;
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

	public Long getTargetNumber() {
		return targetNumber;
	}

	public void setTargetNumber(Long targetNumber) {
		this.targetNumber = targetNumber;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		ActivityGroupUserTarget activityGroupUserTarget = (ActivityGroupUserTarget) o;
		if (activityGroupUserTarget.id == null || id == null) {
			return false;
		}
		return Objects.equals(id, activityGroupUserTarget.id);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(id);
	}

	@Override
	public String toString() {
		return "ActivityGroupUserTarget{" + "id=" + id + ", startDate='" + startDate + "'" + ", endDate='" + endDate
				+ "'" + '}';
	}
}

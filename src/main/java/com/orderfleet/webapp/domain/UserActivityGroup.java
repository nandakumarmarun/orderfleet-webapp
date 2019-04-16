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
 * A UserActivityGroup.
 * 
 * @author Muhammed Riyas T
 * @since July 01, 2016
 */
@Entity
@Table(name = "tbl_user_activity_group")
public class UserActivityGroup implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GenericGenerator(name = "seq_user_activity_group_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "seq_user_activity_group") })
	@GeneratedValue(generator = "seq_user_activity_group_GEN")
	@Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_user_activity_group_id')")
	private Long id;

	@NotNull
	@ManyToOne
	private User user;

	@NotNull
	@ManyToOne
	private ActivityGroup activityGroup;

	@NotNull
	@ManyToOne
	private Company company;

	public UserActivityGroup() {
		super();
	}

	public UserActivityGroup(User user, ActivityGroup activityGroup, Company company) {
		super();
		this.user = user;
		this.activityGroup = activityGroup;
		this.company = company;
	}

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

	public ActivityGroup getActivityGroup() {
		return activityGroup;
	}

	public void setActivityGroup(ActivityGroup activityGroup) {
		this.activityGroup = activityGroup;
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
		UserActivityGroup userActivityGroup = (UserActivityGroup) o;
		if (userActivityGroup.id == null || id == null) {
			return false;
		}
		return Objects.equals(id, userActivityGroup.id);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(id);
	}

}

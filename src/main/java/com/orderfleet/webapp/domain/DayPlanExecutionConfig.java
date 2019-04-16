package com.orderfleet.webapp.domain;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import com.orderfleet.webapp.domain.enums.DayPlanPages;

/**
 * A DayPlanExecutionConfig.
 * 
 * @author Muhammed Riyas T
 * @since Jan 03, 2017
 */
@Entity
@Table(name = "tbl_day_plan_execution_config")
public class DayPlanExecutionConfig implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GenericGenerator(name = "seq_day_plan_execution_config_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "seq_day_plan_execution_config_id") })
	@GeneratedValue(generator = "seq_day_plan_execution_config_id_GEN")
	@Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_day_plan_execution_config_id')")
	private Long id;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "name", nullable = false)
	private DayPlanPages name;

	@NotNull
	@Column(name = "sort_order", nullable = false)
	private int sortOrder;

	@Column(name = "enabled")
	private boolean enabled;

	@ManyToMany
	@JoinTable(name = "tbl_day_plan_execution_config_users", joinColumns = @JoinColumn(name = "day_plan_execution_config_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"))
	private Set<User> users = new HashSet<User>();

	@ManyToOne
	private Company company;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public DayPlanPages getName() {
		return name;
	}

	public void setName(DayPlanPages name) {
		this.name = name;
	}

	public int getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(int sortOrder) {
		this.sortOrder = sortOrder;
	}

	public boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public Set<User> getUsers() {
		return users;
	}

	public void setUsers(Set<User> users) {
		this.users = users;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

}

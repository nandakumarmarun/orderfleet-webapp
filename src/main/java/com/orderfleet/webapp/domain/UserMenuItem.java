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
 * A UserMenuItem.
 * 
 * @author Shaheer
 * @since December 27, 2016
 */
@Entity
@Table(name = "tbl_user_menuitems")
public class UserMenuItem implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GenericGenerator(name = "seq_user_menuitems_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "seq_user_menuitems_id") })
	@GeneratedValue(generator = "seq_user_menuitems_id_GEN")
	@Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_user_menuitems_id')")
	private Long id;

	@NotNull
	@ManyToOne
	private User user;

	@NotNull
	@ManyToOne
	private MenuItem menuItem;
	
	@Column(name = "sort_order", nullable = true)
	private Integer sortOrder;
	
	@NotNull
	@ManyToOne
	private Company company;

	public UserMenuItem() {
		super();
	}
	
	public UserMenuItem(User user, MenuItem menuItem) {
		super();
		this.user = user;
		this.menuItem = menuItem;
		this.company = user.getCompany();
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

	public MenuItem getMenuItem() {
		return menuItem;
	}

	public void setMenuItem(MenuItem menuItem) {
		this.menuItem = menuItem;
	}

	public Integer getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(Integer sortOrder) {
		this.sortOrder = sortOrder;
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
		UserMenuItem userMenuItem = (UserMenuItem) o;
		if (userMenuItem.id == null || id == null) {
			return false;
		}
		return Objects.equals(id, userMenuItem.id);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(id);
	}

}

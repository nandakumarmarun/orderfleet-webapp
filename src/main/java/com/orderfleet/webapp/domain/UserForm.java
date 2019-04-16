package com.orderfleet.webapp.domain;

import java.io.Serializable;

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
 * A UserForm;
 *
 * @author Sarath
 * @since Apr 19, 2017
 *
 */
@Entity
@Table(name = "tbl_user_form")
public class UserForm implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GenericGenerator(name = "seq_user_form_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "seq_user_form_id") })
	@GeneratedValue(generator = "seq_user_form_id_GEN")
	@Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_user_form_id')")
	private Long id;

	@ManyToOne
	@NotNull
	private Company company;

	@ManyToOne
	@NotNull
	private User user;

	@ManyToOne
	@NotNull
	private Form form;

	@NotNull
	@Column(name = "sort_order", nullable = false)
	private Integer sortOrder = 0;

	public UserForm() {
		super();
	}

	public UserForm(Company company, User user, Form form, Integer sortOrder) {
		super();
		this.company = company;
		this.user = user;
		this.form = form;
		this.sortOrder = sortOrder;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Form getForm() {
		return form;
	}

	public void setForm(Form form) {
		this.form = form;
	}

	public Integer getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(Integer sortOrder) {
		this.sortOrder = sortOrder;
	}

	@Override
	public String toString() {
		return "UserForm [ company name=" + company.getLegalName() + ", user name=" + user.getFirstName() + " "
				+ user.getLastName() + ", form name =" + form.getName() + ", sortOrder=" + sortOrder + "]";
	}

}

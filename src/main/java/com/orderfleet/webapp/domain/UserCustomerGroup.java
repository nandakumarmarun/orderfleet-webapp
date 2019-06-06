package com.orderfleet.webapp.domain;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.orderfleet.webapp.domain.enums.DataSourceType;

/**
 * A UserCustomerGroup.
 * 
 * @author Muhammed Riyas T
 * @since June 04, 2019
 */
@Entity
@Table(name = "tbl_user_customer_group")
public class UserCustomerGroup implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GenericGenerator(name = "seq_user_customer_group_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "seq_user_customer_group_id") })
	@GeneratedValue(generator = "seq_user_customer_group_id_GEN")
	@Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_user_customer_group_id')")
	private Long id;

	@NotNull
	@ManyToOne
	private User user;

	@NotNull
	@ManyToOne
	private Stage stage;

	@NotNull
	@ManyToOne
	private Company company;

	public UserCustomerGroup() {
		super();
	}

	public UserCustomerGroup(User user, Stage stage, Company company) {
		super();

		this.user = user;
		this.stage = stage;
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

	public Stage getStage() {
		return stage;
	}

	public void setStage(Stage stage) {
		this.stage = stage;
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
		UserCustomerGroup userStage = (UserCustomerGroup) o;
		if (userStage.id == null || id == null) {
			return false;
		}
		return Objects.equals(id, userStage.id);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(id);
	}

}

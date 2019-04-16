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
 * A User Account Profile
 *
 * @author Sarath
 * @since Oct 24, 2016
 */
@Entity
@Table(name = "tbl_user_account_profile")
public class UserAccountProfile implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GenericGenerator(name = "seq_user_account_profile_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "seq_user_account_profile_id") })
	@GeneratedValue(generator = "seq_user_account_profile_id_GEN")
	@Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_user_account_profile_id')")
	private Long id;

	@ManyToOne
	@NotNull
	private AccountProfile accountProfile;

	@ManyToOne
	@NotNull
	private User user;

	@ManyToOne
	@NotNull
	private Company company;

	public UserAccountProfile() {
		super();
	}

	public UserAccountProfile(AccountProfile accountProfile, User user, Company company) {
		super();
		this.accountProfile = accountProfile;
		this.user = user;
		this.company = company;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public AccountProfile getAccountProfile() {
		return accountProfile;
	}

	public void setAccountProfile(AccountProfile accountProfile) {
		this.accountProfile = accountProfile;
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

	@Override
	public String toString() {
		return "UserAccountProfile [id=" + id + ", accountProfile=" + accountProfile + ", user=" + user + ", company="
				+ company + "]";
	}

}

package com.orderfleet.webapp.domain;

import java.io.Serializable;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "tbl_user_onpremise")
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserOnPremise implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GenericGenerator(name = "uuid-gen", strategy = "uuid2")
	@GeneratedValue(generator = "uuid-gen")
	@org.hibernate.annotations.Type(type = "pg-uuid")
	@Column(name = "id", insertable = false, updatable = false)
	private UUID id;

	private String login;

	@Column(name = "full_name", length = 150)
	private String fullName;

	@NotNull
	@Column(name = "created_date", nullable = false)
	private Instant createdDate = Instant.now();

	@NotNull
	@Column(name = "expire_date", nullable = false)
	private Instant expireDate = ZonedDateTime.now().plusYears(1).toInstant();

	@Column(name = "company_pid", length = 250)
	private String companyPid;

	@Column(name = "company_name", length = 250)
	private String companyName;

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public Instant getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Instant createdDate) {
		this.createdDate = createdDate;
	}

	public Instant getExpireDate() {
		return expireDate;
	}

	public void setExpireDate(Instant expireDate) {
		this.expireDate = expireDate;
	}

	public String getCompanyPid() {
		return companyPid;
	}

	public void setCompanyPid(String companyPid) {
		this.companyPid = companyPid;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

}

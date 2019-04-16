package com.orderfleet.webapp.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

@Entity
@Table(name = "tbl_snrichpartner_company")
public class SnrichPartnerCompany implements Serializable{

	private static final long serialVersionUID = 1L;

	@Id
	@GenericGenerator(name = "seq_snrichpartner_company_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "seq_snrichpartner_company_id") })
	@GeneratedValue(generator = "seq_snrichpartner_company_id_GEN")
	@Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_snrichpartner_company_id')")
	private Long id;

	@NotNull
	@ManyToOne
	private Company company;

	@NotNull
	@ManyToOne
	private SnrichPartner partner;
	
	@Column(name = "db_company_id")
	private String dbCompany;
	
	@Column(name = "users_count")
	private int usersCount;

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

	public SnrichPartner getPartner() {
		return partner;
	}

	public void setPartner(SnrichPartner partner) {
		this.partner = partner;
	}

	public int getUsersCount() {
		return usersCount;
	}

	public void setUsersCount(int usersCount) {
		this.usersCount = usersCount;
	}

	public String getDbCompany() {
		return dbCompany;
	}

	public void setDbCompany(String dbCompany) {
		this.dbCompany = dbCompany;
	}

	
	
	
	
}

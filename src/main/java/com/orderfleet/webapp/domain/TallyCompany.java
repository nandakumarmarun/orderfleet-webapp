package com.orderfleet.webapp.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

/**
 * A TallyCompany.
 *
 * @author Sarath
 * @since Feb 12, 2018
 *
 */
@Entity
@Table(name = "tbl_tally_company")
public class TallyCompany implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GenericGenerator(name = "seq_tally_company_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "seq_tally_company_id") })
	@GeneratedValue(generator = "seq_tally_company_id_GEN")
	@Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_tally_company_id')")
	private Long id;

	@NotNull
	@Size(min = 1, max = 255)
	@Column(name = "tally_company_name", length = 255, nullable = false)
	private String tallyCompanyName;

	@Column(name = "description")
	private String description;

	@ManyToOne
	@NotNull
	private Company company;

	public TallyCompany() {
		super();
	}

	public TallyCompany(Long id, String tallyCompanyName, String description, Company company) {
		super();
		this.id = id;
		this.tallyCompanyName = tallyCompanyName;
		this.description = description;
		this.company = company;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTallyCompanyName() {
		return tallyCompanyName;
	}

	public void setTallyCompanyName(String tallyCompanyName) {
		this.tallyCompanyName = tallyCompanyName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

}

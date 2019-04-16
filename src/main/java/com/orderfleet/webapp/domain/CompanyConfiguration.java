package com.orderfleet.webapp.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import com.orderfleet.webapp.domain.enums.CompanyConfig;

/**
 * A CompanyConfiguration.
 * 
 * @author Muhammed Riyas T
 * @since Jan 05, 2017
 */
@Entity
@Table(name = "tbl_company_configuration")
public class CompanyConfiguration implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GenericGenerator(name = "seq_company_configuration_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "seq_company_configuration_id") })
	@GeneratedValue(generator = "seq_company_configuration_id_GEN")
	@Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_company_configuration_id')")
	private Long id;

	@Enumerated(EnumType.STRING)
	@Column(name = "name")
	private CompanyConfig name;

	@Column(name = "value")
	private String value;

	@Column(name = "description")
	private String description;

	@ManyToOne
	private Company company;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public CompanyConfig getName() {
		return name;
	}

	public void setName(CompanyConfig name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
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

	@Override
	public String toString() {
		return "CompanyConfiguration [id=" + id + ", name=" + name + ", value=" + value + ", description=" + description
				+ ", company=" + company.getLegalName() + "]";
	}

}

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
 * A CompanyInvoiceConfiguration.
 *
 * @author Sarath
 * @since Apr 12, 2018
 *
 */

@Entity
@Table(name = "tbl_company_invoice_configuration")
public class CompanyInvoiceConfiguration implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GenericGenerator(name = "seq_company_invoice_configuration_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "seq_company_invoice_configuration_id") })
	@GeneratedValue(generator = "seq_company_invoice_configuration_id_GEN")
	@Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_company_invoice_configuration_id')")
	private Long id;

	@ManyToOne
	@NotNull
	private Company company;

	@Column(name = "address", updatable = false)
	private String address;

	@Column(name = "amount_per_user", updatable = false)
	private Double amountPerUser;

	public CompanyInvoiceConfiguration() {
		super();
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

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Double getAmountPerUser() {
		return amountPerUser;
	}

	public void setAmountPerUser(Double amountPerUser) {
		this.amountPerUser = amountPerUser;
	}

}

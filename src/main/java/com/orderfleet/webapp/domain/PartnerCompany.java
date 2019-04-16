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
 * a PartnerCompany.
 *
 * @author Sarath
 * @since Feb 23, 2018
 *
 */
@Entity
@Table(name = "tbl_partner_company")
public class PartnerCompany implements Serializable, Cloneable {

	private static final long serialVersionUID = 1L;

	@Id
	@GenericGenerator(name = "seq_partner_company_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "seq_partner_company_id") })
	@GeneratedValue(generator = "seq_partner_company_id_GEN")
	@Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_partner_company_id')")
	private Long id;

	@NotNull
	@ManyToOne
	private Company company;

	@NotNull
	@ManyToOne
	private User partner;

	public PartnerCompany() {
		super();
	}

	public PartnerCompany(Company company, User partner) {
		super();
		this.company = company;
		this.partner = partner;
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

	public User getPartner() {
		return partner;
	}

	public void setPartner(User partner) {
		this.partner = partner;
	}

}

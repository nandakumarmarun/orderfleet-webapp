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
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import com.orderfleet.webapp.domain.enums.PartnerIntegrationSystem;

@Entity
@Table(name = "tbl_snrich_product_company")
public class SnrichProductCompany implements Serializable{

	private static final long serialVersionUID = 1L;

	@Id
	@GenericGenerator(name = "seq_snrich_product_company_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "seq_snrich_product_company_id") })
	@GeneratedValue(generator = "seq_snrich_product_company_id_GEN")
	@Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_snrich_product_company_id')")
	private Long id;
	
	@NotNull
	@ManyToOne
	private SnrichProduct snrichProduct;
	
	@NotNull
	@ManyToOne
	private Company company;
	
	@Column(name = "tally_activation_key")
	private String tallyActivationKey;
	
	@Column(name = "partner_integration", nullable = false, columnDefinition = "character varying DEFAULT 'CLIENT_APP'")
	@Enumerated(EnumType.STRING)
	private PartnerIntegrationSystem partnerIntegrationSystem;
	

	public SnrichProductCompany() {
		super();
	}

	public SnrichProductCompany(SnrichProduct snrichProduct, Company company ,String tallyActivationKey,PartnerIntegrationSystem partnerIntegrationSystem) {
		super();
		this.snrichProduct = snrichProduct;
		this.company = company;
		this.tallyActivationKey = tallyActivationKey;
		this.partnerIntegrationSystem = partnerIntegrationSystem;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public SnrichProduct getSnrichProduct() {
		return snrichProduct;
	}

	public void setSnrichProduct(SnrichProduct snrichProduct) {
		this.snrichProduct = snrichProduct;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public String getTallyActivationKey() {
		return tallyActivationKey;
	}

	public void setTallyActivationKey(String tallyActivationKey) {
		this.tallyActivationKey = tallyActivationKey;
	}

	public PartnerIntegrationSystem getPartnerIntegrationSystem() {
		return partnerIntegrationSystem;
	}

	public void setPartnerIntegrationSystem(PartnerIntegrationSystem partnerIntegrationSystem) {
		this.partnerIntegrationSystem = partnerIntegrationSystem;
	}
	
	
}

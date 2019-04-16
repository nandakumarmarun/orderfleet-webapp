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

import com.orderfleet.webapp.domain.enums.GstAccountType;

@Entity
@Table(name = "tbl_gst_ledger")
public class GstLedger implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GenericGenerator(name = "seq_gst_ledger_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "seq_gst_ledger_id") })
	@GeneratedValue(generator = "seq_gst_ledger_id_GEN")
	@Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_gst_ledger_id')")
	private Long id;
	
	@Column(name = "name")
	private String name;
	
	@ManyToOne
	@NotNull
	private Company company;
	
	@Column(name = "tax_type")
	private String taxType;
	
	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "account_type", nullable = false)
	private GstAccountType accountType;
	
	@Column(name = "tax_rate")
	private double taxRate;
	
	@NotNull
	@Column(name = "activated", nullable = false, columnDefinition = "boolean DEFAULT 'FALSE'")
	private boolean activated;
	
	@Column(name = "guid")
	private String guid;

	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public String getTaxType() {
		return taxType;
	}

	public void setTaxType(String taxType) {
		this.taxType = taxType;
	}

	public GstAccountType getAccountType() {
		return accountType;
	}

	public void setAccountType(GstAccountType accountType) {
		this.accountType = accountType;
	}

	public double getTaxRate() {
		return taxRate;
	}

	public void setTaxRate(double taxRate) {
		this.taxRate = taxRate;
	}
	
	public boolean isActivated() {
		return activated;
	}

	public void setActivated(boolean activated) {
		this.activated = activated;
	}
	
	

	public String getGuid() {
		return guid;
	}

	public void setGuid(String guid) {
		this.guid = guid;
	}

	@Override
	public String toString() {
		return "GstLedger [id=" + id + ", name=" + name + ", company=" + company + ", taxType=" + taxType
				+ ", accountType=" + accountType + ", taxRate=" + taxRate + ", activated=" + activated + ", guid="
				+ guid + "]";
	}

	
	
}

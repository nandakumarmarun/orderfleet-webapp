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
 * a tax master used to handle product taxs.
 *
 * @author Sarath
 * @since Aug 8, 2017
 *
 */

@Entity
@Table(name = "tbl_tax_master")
public class TaxMaster implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GenericGenerator(name = "seq_tax_master_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "seq_tax_master_id") })
	@GeneratedValue(generator = "seq_tax_master_id_GEN")
	@Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_tax_master_id')")
	private Long id;

	@NotNull
	@Column(name = "pid", unique = true, nullable = false, updatable = false)
	private String pid;

	@Column(name = "vat_name", nullable = false)
	private String vatName;

	@Column(name = "description")
	private String description;

	@Column(name = "vat_class")
	private String vatClass;

	@Column(name = "vat_percentage")
	private double vatPercentage;

	@Column(name = "tax_id")
	private String taxId;

	@Column(name = "tax_code")
	private String taxCode;

	@ManyToOne
	@NotNull
	private Company company;

	public TaxMaster() {
		super();
	}

	public TaxMaster(Long id, String pid, String vatName, String description, String vatClass, double vatPercentage,
			Company company) {
		super();
		this.id = id;
		this.pid = pid;
		this.vatName = vatName;
		this.description = description;
		this.vatClass = vatClass;
		this.vatPercentage = vatPercentage;
		this.company = company;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public String getVatName() {
		return vatName;
	}

	public void setVatName(String vatName) {
		this.vatName = vatName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public double getVatPercentage() {
		return vatPercentage;
	}

	public void setVatPercentage(double vatPercentage) {
		this.vatPercentage = vatPercentage;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public String getVatClass() {
		return vatClass;
	}

	public void setVatClass(String vatClass) {
		this.vatClass = vatClass;
	}

	public String getTaxId() {
		return taxId;
	}

	public void setTaxId(String taxId) {
		this.taxId = taxId;
	}

	public String getTaxCode() {
		return taxCode;
	}

	public void setTaxCode(String taxCode) {
		this.taxCode = taxCode;
	}

	@Override
	public String toString() {
		return "TaxMaster [id=" + id + ", pid=" + pid + ", vatName=" + vatName + ", description=" + description
				+ ", vatClass=" + vatClass + ", vatPercentage=" + vatPercentage + ", company=" + company + "]";
	}

}

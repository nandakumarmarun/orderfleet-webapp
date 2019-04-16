package com.orderfleet.webapp.domain;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * A GSTProductGroup for assign pg wise gst.
 *
 * @author Sarath
 * @since Jul 11, 2017
 *
 */

@Entity
@Table(name = "tbl_gst_product_group")
public class GSTProductGroup implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GenericGenerator(name = "seq_product_group_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "seq_product_group_id") })
	@GeneratedValue(generator = "seq_product_group_id_GEN")
	@Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_product_group_id')")
	private Long id;

	@ManyToOne
	@NotNull
	private ProductGroup productGroup;

	@NotNull
	@Column(name = "hsn_sac_code")
	private String hsnsacCode;

	@Column(name = "apply_date")
	private LocalDate applyDate;

	@Column(name = "tax_type")
	private String taxType;

	@Column(name = "integrated_tax")
	private String integratedTax;

	@Column(name = "central_tax")
	private String centralTax;

	@Column(name = "state_tax")
	private String stateTax;

	@Column(name = "aditional_cess")
	private String aditionalCess;

	@ManyToOne
	@NotNull
	private Company company;

	@Column(name = "created_date", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	@JsonIgnore
	private LocalDateTime createdDate = LocalDateTime.now();

	@Column(name = "last_modified_date", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	@JsonIgnore
	private LocalDateTime lastModifiedDate = LocalDateTime.now();

	@PreUpdate
	public void preUpdate() {
		this.lastModifiedDate = LocalDateTime.now();
	}

	public GSTProductGroup() {
		super();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public ProductGroup getProductGroup() {
		return productGroup;
	}

	public void setProductGroup(ProductGroup productGroup) {
		this.productGroup = productGroup;
	}

	public String getHsnsacCode() {
		return hsnsacCode;
	}

	public void setHsnsacCode(String hsnsacCode) {
		this.hsnsacCode = hsnsacCode;
	}

	public LocalDate getApplyDate() {
		return applyDate;
	}

	public void setApplyDate(LocalDate applyDate) {
		this.applyDate = applyDate;
	}

	public String getTaxType() {
		return taxType;
	}

	public void setTaxType(String taxType) {
		this.taxType = taxType;
	}

	public String getIntegratedTax() {
		return integratedTax;
	}

	public void setIntegratedTax(String integratedTax) {
		this.integratedTax = integratedTax;
	}

	public String getCentralTax() {
		return centralTax;
	}

	public void setCentralTax(String centralTax) {
		this.centralTax = centralTax;
	}

	public String getStateTax() {
		return stateTax;
	}

	public void setStateTax(String stateTax) {
		this.stateTax = stateTax;
	}

	public String getAditionalCess() {
		return aditionalCess;
	}

	public void setAditionalCess(String aditionalCess) {
		this.aditionalCess = aditionalCess;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public LocalDateTime getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(LocalDateTime createdDate) {
		this.createdDate = createdDate;
	}

	public LocalDateTime getLastModifiedDate() {
		return lastModifiedDate;
	}

	public void setLastModifiedDate(LocalDateTime lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}

	@Override
	public String toString() {
		return "GSTProductGroup [id=" + id + ", productGroup=" + productGroup + ", hsnsacCode=" + hsnsacCode
				+ ", applyDate=" + applyDate + ", taxType=" + taxType + ", integratedTax=" + integratedTax
				+ ", centralTax=" + centralTax + ", stateTax=" + stateTax + ", aditionalCess=" + aditionalCess
				+ ", company=" + company + ", createdDate=" + createdDate + ", lastModifiedDate=" + lastModifiedDate
				+ "]";
	}

}

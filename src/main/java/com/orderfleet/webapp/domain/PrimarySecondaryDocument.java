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

import com.orderfleet.webapp.domain.enums.VoucherType;

/**
 * A PrimarySecondaryDocument
 * 
 * @author Shaheer
 * @since December 31, 2016
 */
@Entity
@Table(name = "tbl_primary_secondary_document")
public class PrimarySecondaryDocument implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GenericGenerator(name = "seq_primary_secondary_document_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "seq_primary_secondary_document_id") })
	@GeneratedValue(generator = "seq_primary_secondary_document_id_GEN")
	@Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_primary_secondary_document_id')")
	private Long id;


	@ManyToOne
	@NotNull
	private Document document;

	@ManyToOne
	@NotNull
	private Company company;
	
	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "voucher_type", nullable = false)
	private VoucherType voucherType;

	@Column(name = "is_activated", nullable = false)
	private boolean activated = true;

	public PrimarySecondaryDocument() {
		super();
	}

	public PrimarySecondaryDocument(Document document, Company company, VoucherType voucherType, boolean activated) {
		super();
		this.document = document;
		this.company = company;
		this.voucherType = voucherType;
		this.activated = activated;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Document getDocument() {
		return document;
	}

	public void setDocument(Document document) {
		this.document = document;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public VoucherType getVoucherType() {
		return voucherType;
	}

	public void setVoucherType(VoucherType voucherType) {
		this.voucherType = voucherType;
	}

	public boolean isActivated() {
		return activated;
	}

	public void setActivated(boolean activated) {
		this.activated = activated;
	}
}

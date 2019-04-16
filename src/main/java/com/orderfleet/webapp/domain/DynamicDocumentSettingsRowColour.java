package com.orderfleet.webapp.domain;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * A DynamicDocumentSettingsRowColour.
 *
 * @author Sarath
 * @since Aug 28, 2017
 *
 */

@Entity
@Table(name = "tbl_dynamic_document_settings_row_colour")
public class DynamicDocumentSettingsRowColour implements Serializable, Cloneable {

	private static final long serialVersionUID = 1L;

	@Id
	@GenericGenerator(name = "seq_dynamic_document_settings_row_colour_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "seq_dynamic_document_settings_row_colour_id") })
	@GeneratedValue(generator = "seq_dynamic_document_settings_row_colour_id_GEN")
	@Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_dynamic_document_settings_row_colour_id')")
	private Long id;

	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name = "dynamic_document_settings_header_id")
	private DynamicDocumentSettingsHeader dynamicDocumentSettingsHeader;

	@NotNull
	@ManyToOne
	private FormElement formElement;
	
	@NotNull
	@ManyToOne
	private FormElementValue formElementValue;

	@NotNull
	@Column(name = "colour", nullable = false)
	private String colour;

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

	public DynamicDocumentSettingsRowColour() {
		super();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public DynamicDocumentSettingsHeader getDynamicDocumentSettingsHeader() {
		return dynamicDocumentSettingsHeader;
	}

	public void setDynamicDocumentSettingsHeader(DynamicDocumentSettingsHeader dynamicDocumentSettingsHeader) {
		this.dynamicDocumentSettingsHeader = dynamicDocumentSettingsHeader;
	}

	public FormElement getFormElement() {
		return formElement;
	}

	public void setFormElement(FormElement formElement) {
		this.formElement = formElement;
	}

	public String getColour() {
		return colour;
	}

	public void setColour(String colour) {
		this.colour = colour;
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

	public LocalDateTime getLastModifiedDate() {
		return lastModifiedDate;
	}

	public FormElementValue getFormElementValue() {
		return formElementValue;
	}

	public void setFormElementValue(FormElementValue formElementValue) {
		this.formElementValue = formElementValue;
	}

	@Override
	public String toString() {
		return "DynamicDocumentSettingsRowColour [id=" + id + ", dynamicDocumentSettingsHeader="
				+ dynamicDocumentSettingsHeader + ", formElement=" + formElement + ", colour=" + colour + "]";
	}

}

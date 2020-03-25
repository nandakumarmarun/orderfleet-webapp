package com.orderfleet.webapp.domain;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

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
 * A FormFormElement.
 * 
 * @author Muhammed Riyas T
 * @since July 22, 2016
 */
@Entity
@Table(name = "tbl_form_form_element")
public class FormFormElement implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GenericGenerator(name = "seq_form_form_element_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "seq_form_form_element_id") })
	@GeneratedValue(generator = "seq_form_form_element_id_GEN")
	@Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_form_form_element_id')")
	private Long id;

	@NotNull
	@ManyToOne
	private Form form;

	@NotNull
	@ManyToOne
	private FormElement formElement;

	@NotNull
	@Column(name = "sort_order", nullable = false)
	private int sortOrder;

	@NotNull
	@Column(name = "report_order", nullable = false, columnDefinition = "integer DEFAULT 0 ")
	private int reportOrder = 0;

	@NotNull
	@Column(name = "editable", nullable = false)
	private boolean editable = true;

	@Column(name = "validation_enabled", nullable = false)
	private boolean validationEnabled;

	@Column(name = "mandatory", nullable = false, columnDefinition = "boolean DEFAULT 'FALSE'")
	private boolean mandatory;

	@Column(name = "visibility", nullable = false, columnDefinition = "boolean DEFAULT 'TRUE'")
	private boolean visibility = true;

	@Column(name = "dashboard_visibility", nullable = false, columnDefinition = "boolean DEFAULT 'FALSE'")
	private boolean dashboardVisibility;

	@NotNull
	@ManyToOne
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

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Form getForm() {
		return form;
	}

	public void setForm(Form form) {
		this.form = form;
	}

	public FormElement getFormElement() {
		return formElement;
	}

	public void setFormElement(FormElement formElement) {
		this.formElement = formElement;
	}

	public int getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(int sortOrder) {
		this.sortOrder = sortOrder;
	}

	public int getReportOrder() {
		return reportOrder;
	}

	public void setReportOrder(int reportOrder) {
		this.reportOrder = reportOrder;
	}

	public boolean getEditable() {
		return editable;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
	}

	public boolean getValidationEnabled() {
		return validationEnabled;
	}

	public void setValidationEnabled(boolean validationEnabled) {
		this.validationEnabled = validationEnabled;
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

	public boolean getMandatory() {
		return mandatory;
	}

	public void setMandatory(boolean mandatory) {
		this.mandatory = mandatory;
	}

	public boolean getVisibility() {
		return visibility;
	}

	public void setVisibility(boolean visibility) {
		this.visibility = visibility;
	}

	public boolean getDashboardVisibility() {
		return dashboardVisibility;
	}

	public void setDashboardVisibility(boolean dashboardVisibility) {
		this.dashboardVisibility = dashboardVisibility;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		FormFormElement formElement = (FormFormElement) o;
		if (formElement.id == null || id == null) {
			return false;
		}
		return Objects.equals(id, formElement.id);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(id);
	}

}

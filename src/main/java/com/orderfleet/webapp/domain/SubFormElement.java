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
 * A Document.
 * 
 * @author Anish
 * @since March 09, 2020
 */
@Entity
@Table(name = "tbl_sub_form_element")
public class SubFormElement implements Serializable, Cloneable{

	private static final long serialVersionUID = 1L;
	
	@Id
	@GenericGenerator(name = "seq_sub_form_element_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "seq_sub_form_element_id") })
	@GeneratedValue(generator = "seq_sub_form_element_id_GEN")
	@Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_sub_form_element_id')")
	private Long id;
	
	@NotNull
	@ManyToOne
	private Document document;
	
	@NotNull
	@ManyToOne
	private Form form;
	
	@NotNull
	@ManyToOne
	private FormElement formElement;
	
	@NotNull
	@ManyToOne
	private FormElementValue formElementValue;
	
	@NotNull
	@ManyToOne
	private FormElement subFormElement;
	
	@NotNull
	@ManyToOne
	private Company company;

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

	public FormElementValue getFormElementValue() {
		return formElementValue;
	}

	public void setFormElementValue(FormElementValue formElementValue) {
		this.formElementValue = formElementValue;
	}

	public FormElement getSubFormElement() {
		return subFormElement;
	}

	public void setSubFormElement(FormElement subFormElement) {
		this.subFormElement = subFormElement;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}
	
	
}

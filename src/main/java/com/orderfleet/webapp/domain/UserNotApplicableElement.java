package com.orderfleet.webapp.domain;

import java.io.Serializable;
import java.util.Objects;

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
 * A UserNotApplicableElement.
 * 
 * @author Muhammed Riyas T
 * @since Jan 17, 2017
 */
@Entity
@Table(name = "tbl_user_not_applicable_element")
public class UserNotApplicableElement implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GenericGenerator(name = "seq_user_not_applicable_element_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "seq_user_not_applicable_element_id") })
	@GeneratedValue(generator = "seq_user_not_applicable_element_id_GEN")
	@Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_user_not_applicable_element_id')")
	private Long id;

	@NotNull
	@ManyToOne
	private FormElement formElement;

	@NotNull
	@ManyToOne
	private Form form;

	@NotNull
	@ManyToOne
	private Document document;

	@NotNull
	@ManyToOne
	private User user;

	@NotNull
	@ManyToOne
	private Company company;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public FormElement getFormElement() {
		return formElement;
	}

	public void setFormElement(FormElement formElement) {
		this.formElement = formElement;
	}

	public Form getForm() {
		return form;
	}

	public void setForm(Form form) {
		this.form = form;
	}

	public Document getDocument() {
		return document;
	}

	public void setDocument(Document document) {
		this.document = document;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		UserNotApplicableElement userNotApplicableElement = (UserNotApplicableElement) o;
		if (userNotApplicableElement.id == null || id == null) {
			return false;
		}
		return Objects.equals(id, userNotApplicableElement.id);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(id);
	}

}

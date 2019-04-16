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
 * A AccountProfileDynamicDocumentAccountprofile.
 *
 * @author Sarath
 * @since Feb 5, 2018
 *
 */
@Entity
@Table(name = "tbl_accountprofile_dynamicdocument_accountprofile")
public class AccountProfileDynamicDocumentAccountprofile implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GenericGenerator(name = "seq_account_type_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "seq_account_type_id") })
	@GeneratedValue(generator = "seq_account_type_id_GEN")
	@Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_account_type_id')")
	private Long id;

	@ManyToOne
	@NotNull
	private Company company;

	@NotNull
	@Column(name = "account_profle_field", length = 255, nullable = false)
	private String accountProfleField;

	@ManyToOne
	@NotNull
	private FormElement formElement;

	@ManyToOne
	@NotNull
	private Document document;

	@ManyToOne
	@NotNull
	private Form form;

	public AccountProfileDynamicDocumentAccountprofile() {
		super();
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

	public String getAccountProfleField() {
		return accountProfleField;
	}

	public void setAccountProfleField(String accountProfleField) {
		this.accountProfleField = accountProfleField;
	}

	public FormElement getFormElement() {
		return formElement;
	}

	public void setFormElement(FormElement formElement) {
		this.formElement = formElement;
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

	@Override
	public String toString() {
		return "AccountProfile_DynamicDocumentAccountprofile [id=" + id + ", company=" + company
				+ ", accountProfleField=" + accountProfleField + ", formElement=" + formElement + "]";
	}

}

package com.orderfleet.webapp.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

/**
 * A StaticFormJSCode
 * 
 * @author Sarath
 * @since Aug 3, 2016
 */
@Entity
@Table(name = "tbl_static_form_js_code")
public class StaticFormJSCode implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GenericGenerator(name = "seq_static_form_js_code_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "seq_static_form_js_code_id") })
	@GeneratedValue(generator = "seq_static_form_js_code_id_GEN")
	@Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_static_form_js_code_id')")
	private Long id;

	@NotNull
	@Column(name = "js_code", length = 15000, nullable = false)
	private String jsCode;

	@NotNull
	@Size(min = 1, max = 255)
	@Column(name = "js_code_name", nullable = false, length = 255)
	private String jsCodeName;

	@ManyToOne
	@NotNull
	private Document document;

	@ManyToOne
	@NotNull
	private Company company;

	public StaticFormJSCode() {
		super();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getJsCode() {
		return jsCode;
	}

	public void setJsCode(String jsCode) {
		this.jsCode = jsCode;
	}

	public String getJsCodeName() {
		return jsCodeName;
	}

	public void setJsCodeName(String jsCodeName) {
		this.jsCodeName = jsCodeName;
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

	public StaticFormJSCode( String jsCode, String jsCodeName, Document document, Company company) {
		super();
		this.jsCode = jsCode;
		this.jsCodeName = jsCodeName;
		this.document = document;
		this.company = company;
	}

}

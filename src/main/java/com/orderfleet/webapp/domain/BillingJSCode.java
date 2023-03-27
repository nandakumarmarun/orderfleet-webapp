package com.orderfleet.webapp.domain;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Entity
@Table(name = "tbl_billing_js_code")
public class BillingJSCode implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GenericGenerator(name = "seq_billing_js_code_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "seq_billing_js_code_id") })
	@GeneratedValue(generator = "seq_billing_js_code_id_GEN")
	@Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_billing_js_code_id')")
	private Long id;

	@NotNull
	@Column(name = "js_code", length = 15000, nullable = false)
	private String jsCode;

	@Size(min = 1, max = 255)
	@Column(name = "js_code_name", nullable = false, length = 255)
	private String jsCodeName;

	@ManyToOne
	@NotNull
	private Company company;

	public BillingJSCode() {
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


	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public BillingJSCode(String jsCode, String jsCodeName, Document document, Company company) {
		super();
		this.jsCode = jsCode;
		this.jsCodeName = jsCodeName;
		this.company = company;
	}

}

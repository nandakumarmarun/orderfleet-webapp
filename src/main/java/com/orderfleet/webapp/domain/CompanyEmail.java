package com.orderfleet.webapp.domain;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.validator.constraints.Email;

@Entity
@Table(name = "tbl_company_email")
public class CompanyEmail implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Id
	@GenericGenerator(name = "seq_company_email_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "seq_company_email_id") })
	@GeneratedValue(generator = "seq_company_email_id_GEN")
	@Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_company_email_id')")
	private Long id;
	
	@ManyToOne
	private Company company;
	
	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "module_name", nullable = false)
	private ModuleName moduleName;
	
	@NotNull
	@Email
	@Size(max = 100)
	@Column(length = 100, unique = true, nullable = false)
	private String email;
	
	@NotNull
	private String password;
	
	public CompanyEmail() {
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

	public ModuleName getModuleName() {
		return moduleName;
	}

	public void setModuleName(ModuleName moduleName) {
		this.moduleName = moduleName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		CompanyEmail companyEmail = (CompanyEmail) o;
		if (companyEmail.id == null || id == null) {
			return false;
		}
		return Objects.equals(id, companyEmail.id);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(id);
	}
	
	public static enum ModuleName {
		ESTIMATE;
	}
	
}

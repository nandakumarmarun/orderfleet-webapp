package com.orderfleet.webapp.domain;

import java.util.Objects;

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

@Entity
@Table(name = "tbl_account_name_text_settings")
public class AccountNameTextSettings {

	private static final long serialVersionUID = 1L;

	@Id
	@GenericGenerator(name = "seq_account_name_text_settings_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "seq_account_name_text_settings_id") })
	@GeneratedValue(generator = "seq_account_name_text_settings_id_GEN")
	@Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_account_name_text_settings_id')")
	private Long id;

	@NotNull
	@Column(name = "pid", unique = true, nullable = false, updatable = false)
	private String pid;

	@NotNull
	@Size(min = 1, max = 255)
	@Column(name = "name", length = 255, nullable = false)
	private String name;

	@Column(name = "enabled")
	private boolean enabled;

	@ManyToOne
	private Company company;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
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
		AccountNameTextSettings accountNameTextSettings = (AccountNameTextSettings) o;
		if (accountNameTextSettings.id == null || id == null) {
			return false;
		}
		return Objects.equals(id, accountNameTextSettings.id);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(id);
	}
	
	

}

package com.orderfleet.webapp.domain;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

@Entity
@Table(name = "tbl_company_integration_module")
public class CompanyIntegrationModule implements Serializable {

    private static final long serialVersionUID = 1L;

	@Id
	@GenericGenerator(name = "seq_company_integration_module_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "seq_company_integration_module_id") })
	@GeneratedValue(generator = "seq_company_integration_module_id_GEN")
	@Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_company_integration_module_id')")
	private Long id;
	
	@NotNull
	@ManyToOne
	@JoinColumn(name = "company_id", nullable = false)
	private Company company;
	
	@NotNull
	@ManyToOne
	@JoinColumn(name = "module_name", nullable = false)
	@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
	private IntegrationModule integrationModule;
	
	@NotNull
	@Column(name = "base_url",nullable = false)
	private String baseUrl;
	
	@NotNull
	@Column(name = "created_date", nullable = false)
	private LocalDateTime createdDate;
	
	@Column(name = "auto_update", nullable = false, columnDefinition = "boolean DEFAULT false")
	private boolean autoUpdate = false;

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

	public IntegrationModule getIntegrationModule() {
		return integrationModule;
	}

	public void setIntegrationModule(IntegrationModule integrationModule) {
		this.integrationModule = integrationModule;
	}

	public String getBaseUrl() {
		return baseUrl;
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	public LocalDateTime getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(LocalDateTime createdDate) {
		this.createdDate = createdDate;
	}

	public boolean getAutoUpdate() {
		return autoUpdate;
	}

	public void setAutoUpdate(boolean autoUpdate) {
		this.autoUpdate = autoUpdate;
	}

	@Override
	public String toString() {
		return "CompanyIntegrationModule [id=" + id + ", company=" + company + ", integrationModule="
				+ integrationModule + ", baseUrl=" + baseUrl + ", createdDate=" + createdDate + "]";
	}
	
	
	
}
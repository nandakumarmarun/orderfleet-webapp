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
import org.hibernate.annotations.Type;

import com.orderfleet.webapp.domain.model.DashboardConfiguration;
import com.orderfleet.webapp.domain.model.PrintConfiguration;
import com.orderfleet.webapp.domain.model.SalesConfiguration;

/**
 * A CompanySetting.
 * 
 * @author Shaheer
 * @since October 18, 2016
 */
@Entity
@Table(name = "tbl_company_setting")
public class CompanySetting implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GenericGenerator(name = "seq_company_setting_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "seq_company_setting_id") })
	@GeneratedValue(generator = "seq_company_setting_id_GEN")
	@Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_company_setting_id')")
	private Long id;

	@Column(name = "dashboardConfiguration")
	@Type(type = "com.orderfleet.webapp.domain.usertype.ObjectType", parameters = {
			@org.hibernate.annotations.Parameter(name = "type", value = "classType"),
			@org.hibernate.annotations.Parameter(name = "element", value = "com.orderfleet.webapp.domain.model.DashboardConfiguration") })
	private DashboardConfiguration dashboardConfiguration;

	@Column(name = "salesConfiguration")
	@Type(type = "com.orderfleet.webapp.domain.usertype.ObjectType", parameters = {
			@org.hibernate.annotations.Parameter(name = "type", value = "classType"),
			@org.hibernate.annotations.Parameter(name = "element", value = "com.orderfleet.webapp.domain.model.SalesConfiguration") })
	private SalesConfiguration salesConfiguration;

	@Column(name = "printConfiguration")
	@Type(type = "com.orderfleet.webapp.domain.usertype.ObjectType", parameters = {
			@org.hibernate.annotations.Parameter(name = "type", value = "classType"),
			@org.hibernate.annotations.Parameter(name = "element", value = "com.orderfleet.webapp.domain.model.PrintConfiguration") })
	private PrintConfiguration printConfiguration;

	@ManyToOne
	@NotNull
	private Company company;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public DashboardConfiguration getDashboardConfiguration() {
		return dashboardConfiguration;
	}

	public void setDashboardConfiguration(DashboardConfiguration dashboardConfiguration) {
		this.dashboardConfiguration = dashboardConfiguration;
	}

	public SalesConfiguration getSalesConfiguration() {
		return salesConfiguration;
	}

	public void setSalesConfiguration(SalesConfiguration salesConfiguration) {
		this.salesConfiguration = salesConfiguration;
	}

	public PrintConfiguration getPrintConfiguration() {
		return printConfiguration;
	}

	public void setPrintConfiguration(PrintConfiguration printConfiguration) {
		this.printConfiguration = printConfiguration;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

}

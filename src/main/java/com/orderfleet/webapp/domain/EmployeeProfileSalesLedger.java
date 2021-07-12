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
 * A EmployeeProfileLocation.
 * 
 * @author Muhammed Riyas T
 * @since August 31, 2016
 */
@Entity
@Table(name = "tbl_employee_profile_sales_ledger")
public class EmployeeProfileSalesLedger implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GenericGenerator(name = "seq_employee_profile_sales_ledger_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "seq_employee_profile_sales_ledger_id") })
	@GeneratedValue(generator = "seq_employee_profile_sales_ledger_id_GEN")
	@Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_employee_profile_sales_ledger_id')")
	private Long id;

	@NotNull
	@ManyToOne
	private EmployeeProfile employeeProfile;

	@NotNull
	@ManyToOne
	private SalesLedger salesLedger;

	@NotNull
	@ManyToOne
	private Company company;

	public EmployeeProfileSalesLedger() {
		super();
	}

	public EmployeeProfileSalesLedger(EmployeeProfile employeeProfile, SalesLedger salesLedger) {
		super();
		this.employeeProfile = employeeProfile;
		this.salesLedger = salesLedger;
		this.company = employeeProfile.getCompany();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public EmployeeProfile getEmployeeProfile() {
		return employeeProfile;
	}

	public void setEmployeeProfile(EmployeeProfile employeeProfile) {
		this.employeeProfile = employeeProfile;
	}

	public SalesLedger getSalesLedger() {
		return salesLedger;
	}

	public void setSalesLedger(SalesLedger salesLedger) {
		this.salesLedger = salesLedger;
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
		EmployeeProfileSalesLedger employeeProfileSalesLedger = (EmployeeProfileSalesLedger) o;
		if (employeeProfileSalesLedger.id == null || id == null) {
			return false;
		}
		return Objects.equals(id, employeeProfileSalesLedger.id);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(id);
	}

}

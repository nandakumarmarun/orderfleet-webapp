package com.orderfleet.webapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.orderfleet.webapp.domain.enums.BillingPeriod;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDate;

@Entity
@Table(name = "tbl_company_billing")
public class CompanyBilling implements Serializable, Cloneable {
	
	private static final long serialVersionUID = 1L;
	
	@Id
	@GenericGenerator(name = "seq_company_billing_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "seq_company_billing_id") })
	@GeneratedValue(generator = "seq_company_billing_id_GEN")
	
	@Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_company_billing_id')")
	private Long id;

	@NotNull
	@Column(name = "pid", unique = true, nullable = false, updatable = false)
	private String pid;
	
	@ManyToOne
	Company company;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "billing_period")
	private BillingPeriod billingPeriod;
	
	@Column(name = "no_of_months")
	private String noOfMonths;
	
	@Column(name = "last_billed_date", nullable = false)
	@JsonIgnore
	private LocalDate lastBilledDate;

	@Column(name = "due_date", nullable = false)
	@JsonIgnore
	private LocalDate next_bill_date;

	public CompanyBilling() {
		super();
		// TODO Auto-generated constructor stub
	}

	public CompanyBilling(Long id, Company company, BillingPeriod billingPeriod, String noOfMonths,
                          LocalDate lastBilledDate, LocalDate next_bill_date) {
		super();
		this.company = company;
		this.billingPeriod = billingPeriod;
		this.noOfMonths = noOfMonths;
		this.lastBilledDate = lastBilledDate;
		this.next_bill_date = next_bill_date;
	}

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

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public BillingPeriod getBillingPeriod() {
		return billingPeriod;
	}

	public void setBillingPeriod(BillingPeriod billingPeriod) {
		this.billingPeriod = billingPeriod;
	}

	public String getNoOfMonths() {
		return noOfMonths;
	}

	public void setNoOfMonths(String noOfMonths) {
		this.noOfMonths = noOfMonths;
	}

	public LocalDate getLastBilledDate() {
		return lastBilledDate;
	}

	public void setLastBilledDate(LocalDate lastBilledDate) {
		this.lastBilledDate = lastBilledDate;
	}

	public LocalDate getNext_bill_date() {
		return next_bill_date;
	}

	public void setNext_bill_date(LocalDate next_bill_date) {
		this.next_bill_date = next_bill_date;
	}

	@Override
	public String toString() {
		return "CompanyBilling [company=" + company + ", billingPeriod=" + billingPeriod + ", noOfMonths=" + noOfMonths
				+ ", lastBilledDate=" + lastBilledDate + ", next_bill_date=" + next_bill_date + "]";
	}

 
	
	
}

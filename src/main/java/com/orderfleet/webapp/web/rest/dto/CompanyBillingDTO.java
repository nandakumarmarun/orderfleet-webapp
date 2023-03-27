package com.orderfleet.webapp.web.rest.dto;

import com.orderfleet.webapp.domain.CompanyBilling;
import com.orderfleet.webapp.domain.enums.BillingPeriod;

import java.time.LocalDate;

public class CompanyBillingDTO {

	private String pid;

	private String companyPid;

	private String companyName;

	private String noOfMonths;

	private BillingPeriod billingPeriod;

	private LocalDate lastBillDate;

	private LocalDate dueBillDate;
	
	

	public CompanyBillingDTO() {
		super();
		// TODO Auto-generated constructor stub
	}

	public CompanyBillingDTO(String companyName, String noOfMonths, BillingPeriod billingPeriod,
			LocalDate lastBillDate, LocalDate dueBillDate) {
		super();
		this.companyName = companyName;
		this.noOfMonths = noOfMonths;
		this.billingPeriod = billingPeriod;
		this.lastBillDate = lastBillDate;
		this.dueBillDate = dueBillDate;
	}

	public CompanyBillingDTO(CompanyBilling billing) {
		
		// TODO Auto-generated constructor stub
		this.companyPid = billing.getCompany().getPid();
		this.companyName = billing.getCompany().getLegalName();
		this.noOfMonths = billing.getNoOfMonths();
		this.billingPeriod = billing.getBillingPeriod();
		this.lastBillDate = billing.getLastBilledDate();
		this.dueBillDate = billing.getNext_bill_date();
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public String getCompanyPid() {
		return companyPid;
	}

	public void setCompanyPid(String companyPid) {
		this.companyPid = companyPid;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getNoOfMonths() {
		return noOfMonths;
	}

	public void setNoOfMonths(String noOfMonths) {
		this.noOfMonths = noOfMonths;
	}

	public BillingPeriod getBillingPeriod() {
		return billingPeriod;
	}

	public void setBillingPeriod(BillingPeriod billingPeriod) {
		this.billingPeriod = billingPeriod;
	}

	public LocalDate getLastBillDate() {
		return lastBillDate;
	}

	public void setLastBillDate(LocalDate lastBillDate) {
		this.lastBillDate = lastBillDate;
	}

	public LocalDate getDueBillDate() {
		return dueBillDate;
	}

	public void setDueBillDate(LocalDate dueBillDate) {
		this.dueBillDate = dueBillDate;
	}

	@Override
	public String toString() {
		return "CompanyBillingDTO [pid=" + pid + ", companyPid=" + companyPid + ", companyName=" + companyName
				+ ", noOfMonths=" + noOfMonths + ", billingPeriod=" + billingPeriod + ", lastBillDate=" + lastBillDate
				+ ", dueBillDate=" + dueBillDate + "]";
	}

	

}

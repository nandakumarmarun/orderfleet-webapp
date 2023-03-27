package com.orderfleet.webapp.web.rest.dto;

import java.time.LocalDate;
import java.util.List;

public class ActiveSubscribersBillingDTO {
	
	private String pid;

	private String companyPid;

	private String companyName;

	private Integer countOfActiveUser;

	private LocalDate fromDate;

	private LocalDate toDate;

	private String noOfMonth;

	private Double Total;
	
	private List<BillingDetail> billingDetail;

	public ActiveSubscribersBillingDTO() {
		super();
		// TODO Auto-generated constructor stub
	}

	public ActiveSubscribersBillingDTO(String pid,String companyPid, String companyName, Integer countOfActiveUser,
			LocalDate fromDate, LocalDate toDate, String noOfMonth, Double total, List<BillingDetail> billingDetail) {
		super();
		this.pid = pid;
		this.companyPid = companyPid;
		this.companyName = companyName;
		this.countOfActiveUser = countOfActiveUser;
		this.fromDate = fromDate;
		this.toDate = toDate;
		this.noOfMonth = noOfMonth;
		this.Total = total;

	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public Integer getCountOfActiveUser() {
		return countOfActiveUser;
	}

	public void setCountOfActiveUser(Integer countOfActiveUser) {
		this.countOfActiveUser = countOfActiveUser;
	}

	public LocalDate getFromDate() {
		return fromDate;
	}

	public void setFromDate(LocalDate fromDate) {
		this.fromDate = fromDate;
	}

	public LocalDate getToDate() {
		return toDate;
	}

	public void setToDate(LocalDate toDate) {
		this.toDate = toDate;
	}

	public String getNoOfMonth() {
		return noOfMonth;
	}

	public void setNoOfMonth(String noOfMonth) {
		this.noOfMonth = noOfMonth;
	}

	public Double getTotal() {
		return Total;
	}

	public void setTotal(Double total) {
		Total = total;
	}

	public String getCompanyPid() {
		return companyPid;
	}

	public void setCompanyPid(String companyPid) {
		this.companyPid = companyPid;
	}

	
	public List<BillingDetail> getBillingDetail() {
		return billingDetail;
	}

	public void setBillingDetail(List<BillingDetail> billingDetail) {
		this.billingDetail = billingDetail;
	}

	@Override
	public String toString() {
		return "ActiveSubscribersBillingDTO [companyName=" + companyName + ", countOfActiveUser=" + countOfActiveUser
				+ ", fromDate=" + fromDate + ", toDate=" + toDate + ", noOfMonth=" + noOfMonth + ", Total=" + Total + "]";
	}

}

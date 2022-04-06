package com.orderfleet.webapp.web.rest.dto;

import java.time.LocalDateTime;

public class VisitDetailReportView {
	
     private String pid;
	
	private LocalDateTime firstVisitTime;
	
	private LocalDateTime lastVisitTime;
	
	private String totalVisit;
	
	private LocalDateTime firstSoTime;
	
	private LocalDateTime lastSoTime;
	
	private String totalSo;
	
	private Integer ledgerCount;
	
	private double totalKG;
	
	private double totalReceiptAmount;
	
	private double totalSaleOrderAmount;

	public VisitDetailReportView() {
		super();
		// TODO Auto-generated constructor stub
	}

	
	
	public VisitDetailReportView(String pid, LocalDateTime firstVisitTime, LocalDateTime lastVisitTime,
			String totalVisit) {
		super();
		this.pid = pid;
		this.firstVisitTime = firstVisitTime;
		this.lastVisitTime = lastVisitTime;
		this.totalVisit = totalVisit;
	}



	public VisitDetailReportView(String pid, LocalDateTime firstSoTime, LocalDateTime lastSoTime, String totalSo,
			double totalSaleOrderAmount) {
		super();
		this.pid = pid;
		this.firstSoTime = firstSoTime;
		this.lastSoTime = lastSoTime;
		this.totalSo = totalSo;
		this.totalSaleOrderAmount = totalSaleOrderAmount;
	}



	


	public VisitDetailReportView( double totalReceiptAmount) {
		super();
	
		this.totalReceiptAmount = totalReceiptAmount;
	}



	public LocalDateTime getFirstVisitTime() {
		return firstVisitTime;
	}

	public void setFirstVisitTime(LocalDateTime firstVisitTime) {
		this.firstVisitTime = firstVisitTime;
	}

	public LocalDateTime getLastVisitTime() {
		return lastVisitTime;
	}

	public void setLastVisitTime(LocalDateTime lastVisitTime) {
		this.lastVisitTime = lastVisitTime;
	}

	public String getTotalVisit() {
		return totalVisit;
	}

	public void setTotalVisit(String totalVisit) {
		this.totalVisit = totalVisit;
	}

	public LocalDateTime getFirstSoTime() {
		return firstSoTime;
	}

	public void setFirstSoTime(LocalDateTime firstSoTime) {
		this.firstSoTime = firstSoTime;
	}

	public LocalDateTime getLastSoTime() {
		return lastSoTime;
	}

	public void setLastSoTime(LocalDateTime lastSoTime) {
		this.lastSoTime = lastSoTime;
	}

	public String getTotalSo() {
		return totalSo;
	}

	public void setTotalSo(String totalSo) {
		this.totalSo = totalSo;
	}

	public Integer getLedgerCount() {
		return ledgerCount;
	}

	public void setLedgerCount(Integer i) {
		this.ledgerCount = i;
	}

	public double getTotalKG() {
		return totalKG;
	}

	public void setTotalKG(double totalKG) {
		this.totalKG = totalKG;
	}

	public double getTotalReceiptAmount() {
		return totalReceiptAmount;
	}

	public void setTotalReceiptAmount(double totalReceiptAmount) {
		this.totalReceiptAmount = totalReceiptAmount;
	}

	public double getTotalSaleOrderAmount() {
		return totalSaleOrderAmount;
	}

	public void setTotalSaleOrderAmount(double totalSaleOrderAmount) {
		this.totalSaleOrderAmount = totalSaleOrderAmount;
	}
	
}

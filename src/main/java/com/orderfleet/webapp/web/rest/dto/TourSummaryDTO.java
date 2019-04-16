package com.orderfleet.webapp.web.rest.dto;

import java.time.LocalDate;

/**
 * A Tour summery For Hycount
 *
 * @author Sarath
 * @since Nov 11, 2016
 */
public class TourSummaryDTO {

	private String dynamicDocumentPid;
	private String voucherNo;
	private Long voucherId;
	private LocalDate tourDate;
	private String repCode;
	private String startingStation;
	private double startingKm;
	private double closingKm;
	private String remarks;
	private String haltingStation;
	private double runningKm;
	private double fuel;
	private String stationCovered;
	private String startingTime;
	private String haltingTime;

	public TourSummaryDTO() {
		super();
	}

	public TourSummaryDTO(String voucherNo, Long voucherId, LocalDate tourDate, String repCode, String startingStation,
			double startingKm, double closingKm, String remarks, String haltingStation, double runningKm, double fuel,
			String stationCovered, String startingTime, String haltingTime) {
		super();
		this.voucherNo = voucherNo;
		this.voucherId = voucherId;
		this.tourDate = tourDate;
		this.repCode = repCode;
		this.startingStation = startingStation;
		this.startingKm = startingKm;
		this.closingKm = closingKm;
		this.remarks = remarks;
		this.haltingStation = haltingStation;
		this.runningKm = runningKm;
		this.fuel = fuel;
		this.stationCovered = stationCovered;
		this.startingTime = startingTime;
		this.haltingTime = haltingTime;
	}

	public String getVoucherNo() {
		return voucherNo;
	}

	public void setVoucherNo(String voucherNo) {
		this.voucherNo = voucherNo;
	}

	public Long getVoucherId() {
		return voucherId;
	}

	public void setVoucherId(Long voucherId) {
		this.voucherId = voucherId;
	}

	public LocalDate getTourDate() {
		return tourDate;
	}

	public void setTourDate(LocalDate tourDate) {
		this.tourDate = tourDate;
	}

	public String getRepCode() {
		return repCode;
	}

	public void setRepCode(String repCode) {
		this.repCode = repCode;
	}

	public String getStartingStation() {
		return startingStation;
	}

	public void setStartingStation(String startingStation) {
		this.startingStation = startingStation;
	}

	public double getStartingKm() {
		return startingKm;
	}

	public void setStartingKm(double startingKm) {
		this.startingKm = startingKm;
	}

	public double getClosingKm() {
		return closingKm;
	}

	public void setClosingKm(double closingKm) {
		this.closingKm = closingKm;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getHaltingStation() {
		return haltingStation;
	}

	public void setHaltingStation(String haltingStation) {
		this.haltingStation = haltingStation;
	}

	public double getRunningKm() {
		return runningKm;
	}

	public void setRunningKm(double runningKm) {
		this.runningKm = runningKm;
	}

	public double getFuel() {
		return fuel;
	}

	public void setFuel(double fuel) {
		this.fuel = fuel;
	}

	public String getStationCovered() {
		return stationCovered;
	}

	public void setStationCovered(String stationCovered) {
		this.stationCovered = stationCovered;
	}

	public String getStartingTime() {
		return startingTime;
	}

	public void setStartingTime(String startingTime) {
		this.startingTime = startingTime;
	}

	public String getHaltingTime() {
		return haltingTime;
	}

	public void setHaltingTime(String haltingTime) {
		this.haltingTime = haltingTime;
	}

	public String getDynamicDocumentPid() {
		return dynamicDocumentPid;
	}

	public void setDynamicDocumentPid(String dynamicDocumentPid) {
		this.dynamicDocumentPid = dynamicDocumentPid;
	}

	@Override
	public String toString() {
		return "TourSummaryDTO [voucherNo=" + voucherNo + ", voucherId=" + voucherId + ", tourDate=" + tourDate
				+ ", repCode=" + repCode + ", startingStation=" + startingStation + ", startingKm=" + startingKm
				+ ", closingKm=" + closingKm + ", remarks=" + remarks + ", haltingStation=" + haltingStation
				+ ", runningKm=" + runningKm + ", fuel=" + fuel + ", stationCovered=" + stationCovered
				+ ", startingTime=" + startingTime + ", haltingTime=" + haltingTime + "]";
	}

}

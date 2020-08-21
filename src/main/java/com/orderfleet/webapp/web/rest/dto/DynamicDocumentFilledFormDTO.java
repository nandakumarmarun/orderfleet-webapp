package com.orderfleet.webapp.web.rest.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.orderfleet.webapp.domain.DynamicDocumentHeader;
import com.orderfleet.webapp.domain.DynamicDocumentHeaderHistory;
import com.orderfleet.webapp.domain.FilledFormDetail;
import com.orderfleet.webapp.domain.enums.TallyDownloadStatus;

/**
 * A DTO for the InventoryVoucherHeader entity.
 * 
 * @author Sarath
 * @since Nov 4, 2016
 */
public class DynamicDocumentFilledFormDTO {

	private String dynamicDocumentHeaderAccountProfileName;

	private String dynamicDocumentHeaderAccountProfilePhone1;

	private String dynamicDocumentHeaderAccountProfileCity;

	private String dynamicDocumentHeaderAccountProfileAddress;

	private String dynamicDocumentHeaderAccountProfileLocation;

	private String dynamicDocumentHeaderCreatedByFirstName;

	private String dynamicDocumentHeaderEmployeeName;

	private LocalDateTime dynamicDocumentHeaderDocumentDate;

	private String dynamicDocumentHeaderTaskExecutionLocation;

	private List<DynamicDocumentFilledFormDetailsDTO> filledFormDetails;

	public String getDynamicDocumentHeaderAccountProfileName() {
		return dynamicDocumentHeaderAccountProfileName;
	}

	public void setDynamicDocumentHeaderAccountProfileName(String dynamicDocumentHeaderAccountProfileName) {
		this.dynamicDocumentHeaderAccountProfileName = dynamicDocumentHeaderAccountProfileName;
	}

	public String getDynamicDocumentHeaderAccountProfilePhone1() {
		return dynamicDocumentHeaderAccountProfilePhone1;
	}

	public void setDynamicDocumentHeaderAccountProfilePhone1(String dynamicDocumentHeaderAccountProfilePhone1) {
		this.dynamicDocumentHeaderAccountProfilePhone1 = dynamicDocumentHeaderAccountProfilePhone1;
	}

	public String getDynamicDocumentHeaderAccountProfileCity() {
		return dynamicDocumentHeaderAccountProfileCity;
	}

	public void setDynamicDocumentHeaderAccountProfileCity(String dynamicDocumentHeaderAccountProfileCity) {
		this.dynamicDocumentHeaderAccountProfileCity = dynamicDocumentHeaderAccountProfileCity;
	}

	public String getDynamicDocumentHeaderAccountProfileLocation() {
		return dynamicDocumentHeaderAccountProfileLocation;
	}

	public void setDynamicDocumentHeaderAccountProfileLocation(String dynamicDocumentHeaderAccountProfileLocation) {
		this.dynamicDocumentHeaderAccountProfileLocation = dynamicDocumentHeaderAccountProfileLocation;
	}

	public String getDynamicDocumentHeaderCreatedByFirstName() {
		return dynamicDocumentHeaderCreatedByFirstName;
	}

	public void setDynamicDocumentHeaderCreatedByFirstName(String dynamicDocumentHeaderCreatedByFirstName) {
		this.dynamicDocumentHeaderCreatedByFirstName = dynamicDocumentHeaderCreatedByFirstName;
	}

	public String getDynamicDocumentHeaderEmployeeName() {
		return dynamicDocumentHeaderEmployeeName;
	}

	public void setDynamicDocumentHeaderEmployeeName(String dynamicDocumentHeaderEmployeeName) {
		this.dynamicDocumentHeaderEmployeeName = dynamicDocumentHeaderEmployeeName;
	}

	public LocalDateTime getDynamicDocumentHeaderDocumentDate() {
		return dynamicDocumentHeaderDocumentDate;
	}

	public void setDynamicDocumentHeaderDocumentDate(LocalDateTime dynamicDocumentHeaderDocumentDate) {
		this.dynamicDocumentHeaderDocumentDate = dynamicDocumentHeaderDocumentDate;
	}

	public String getDynamicDocumentHeaderTaskExecutionLocation() {
		return dynamicDocumentHeaderTaskExecutionLocation;
	}

	public void setDynamicDocumentHeaderTaskExecutionLocation(String dynamicDocumentHeaderTaskExecutionLocation) {
		this.dynamicDocumentHeaderTaskExecutionLocation = dynamicDocumentHeaderTaskExecutionLocation;
	}

	public List<DynamicDocumentFilledFormDetailsDTO> getFilledFormDetails() {
		return filledFormDetails;
	}

	public void setFilledFormDetails(List<DynamicDocumentFilledFormDetailsDTO> filledFormDetails) {
		this.filledFormDetails = filledFormDetails;
	}

	public String getDynamicDocumentHeaderAccountProfileAddress() {
		return dynamicDocumentHeaderAccountProfileAddress;
	}

	public void setDynamicDocumentHeaderAccountProfileAddress(String dynamicDocumentHeaderAccountProfileAddress) {
		this.dynamicDocumentHeaderAccountProfileAddress = dynamicDocumentHeaderAccountProfileAddress;
	}

}

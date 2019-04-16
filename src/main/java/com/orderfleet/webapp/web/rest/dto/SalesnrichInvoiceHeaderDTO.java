package com.orderfleet.webapp.web.rest.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.orderfleet.webapp.domain.SalesnrichInvoiceHeader;

/**
 * A DTO for the SalesNrichInvoice entity.
 *
 * @author Sarath
 * @since Mar 15, 2018
 *
 */
public class SalesnrichInvoiceHeaderDTO {

	private Long id;
	private Long invoiceNumber;
	private LocalDate invoiceDate;
	private LocalDate billingFrom;
	private LocalDate billingTo;
	private Long activeUserCount;
	private Long checkedUserCount;
	private Long totalUserCount;
	private double gstPercentage;
	private double gstAmount;
	private double subTotal;
	private double totalAmount;
	private String companyPid;
	private String companyName;
	private List<SalesnrichInvoiceDetailDTO> salesnrichInvoiceDetailDTOs;
	private LocalDateTime createdDate = LocalDateTime.now();
	private LocalDateTime lastModifiedDate = LocalDateTime.now();

	public SalesnrichInvoiceHeaderDTO() {
		super();
	}

	public SalesnrichInvoiceHeaderDTO(SalesnrichInvoiceHeader salesnrichInvoiceHeader) {
		super();
		this.id = salesnrichInvoiceHeader.getId();
		this.invoiceNumber = salesnrichInvoiceHeader.getInvoiceNumber();
		this.invoiceDate = salesnrichInvoiceHeader.getInvoiceDate();
		this.billingFrom = salesnrichInvoiceHeader.getBillingFrom();
		this.billingTo = salesnrichInvoiceHeader.getBillingTo();
		this.activeUserCount = salesnrichInvoiceHeader.getActiveUserCount();
		this.checkedUserCount = salesnrichInvoiceHeader.getCheckedUserCount();
		this.totalUserCount = salesnrichInvoiceHeader.getTotalUserCount();
		this.gstPercentage = salesnrichInvoiceHeader.getGstPercentage();
		this.gstAmount = salesnrichInvoiceHeader.getGstAmount();
		this.subTotal = salesnrichInvoiceHeader.getSubTotal();
		this.totalAmount = salesnrichInvoiceHeader.getTotalAmount();
		this.companyName = salesnrichInvoiceHeader.getCompany().getLegalName();
		this.companyPid = salesnrichInvoiceHeader.getCompany().getPid();
		if (salesnrichInvoiceHeader.getSalesnrichInvoiceDetail() != null) {
			this.salesnrichInvoiceDetailDTOs = salesnrichInvoiceHeader.getSalesnrichInvoiceDetail().stream()
					.map(SalesnrichInvoiceDetailDTO::new).collect(Collectors.toList());
		}
		this.createdDate = salesnrichInvoiceHeader.getCreatedDate();
		this.lastModifiedDate = salesnrichInvoiceHeader.getLastModifiedDate();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getInvoiceNumber() {
		return invoiceNumber;
	}

	public void setInvoiceNumber(Long invoiceNumber) {
		this.invoiceNumber = invoiceNumber;
	}

	public LocalDate getInvoiceDate() {
		return invoiceDate;
	}

	public void setInvoiceDate(LocalDate invoiceDate) {
		this.invoiceDate = invoiceDate;
	}

	public LocalDate getBillingFrom() {
		return billingFrom;
	}

	public void setBillingFrom(LocalDate billingFrom) {
		this.billingFrom = billingFrom;
	}

	public LocalDate getBillingTo() {
		return billingTo;
	}

	public void setBillingTo(LocalDate billingTo) {
		this.billingTo = billingTo;
	}

	public Long getActiveUserCount() {
		return activeUserCount;
	}

	public void setActiveUserCount(Long activeUserCount) {
		this.activeUserCount = activeUserCount;
	}

	public Long getCheckedUserCount() {
		return checkedUserCount;
	}

	public void setCheckedUserCount(Long checkedUserCount) {
		this.checkedUserCount = checkedUserCount;
	}

	public Long getTotalUserCount() {
		return totalUserCount;
	}

	public void setTotalUserCount(Long totalUserCount) {
		this.totalUserCount = totalUserCount;
	}

	public double getGstPercentage() {
		return gstPercentage;
	}

	public void setGstPercentage(double gstPercentage) {
		this.gstPercentage = gstPercentage;
	}

	public double getGstAmount() {
		return gstAmount;
	}

	public void setGstAmount(double gstAmount) {
		this.gstAmount = gstAmount;
	}

	public double getSubTotal() {
		return subTotal;
	}

	public void setSubTotal(double subTotal) {
		this.subTotal = subTotal;
	}

	public double getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(double totalAmount) {
		this.totalAmount = totalAmount;
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

	public List<SalesnrichInvoiceDetailDTO> getSalesnrichInvoiceDetailDTOs() {
		return salesnrichInvoiceDetailDTOs;
	}

	public void setSalesnrichInvoiceDetailDTOs(List<SalesnrichInvoiceDetailDTO> salesnrichInvoiceDetailDTOs) {
		this.salesnrichInvoiceDetailDTOs = salesnrichInvoiceDetailDTOs;
	}

	public LocalDateTime getCreatedDate() {
		return createdDate;
	}

	public LocalDateTime getLastModifiedDate() {
		return lastModifiedDate;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		SalesnrichInvoiceHeaderDTO salesNrichInvoiceDTO = (SalesnrichInvoiceHeaderDTO) o;

		if (!Objects.equals(invoiceNumber, salesNrichInvoiceDTO.invoiceNumber))
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(invoiceNumber);
	}

}

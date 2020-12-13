package com.orderfleet.webapp.web.rest.api.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.orderfleet.webapp.domain.AccountProfileGeoLocationTagging;
import com.orderfleet.webapp.domain.enums.GeoTaggingType;

/**
 * A DTO For AccountProfileGeoLocation Entity.
 *
 * @author fahad
 * @since Jul 6, 2017
 */
public class SalesOrderAllocationDTO {

	private String documentNumber;

	private String documentDate;

	private double documentAmount;

	public String getDocumentNumber() {
		return documentNumber;
	}

	public void setDocumentNumber(String documentNumber) {
		this.documentNumber = documentNumber;
	}

	public String getDocumentDate() {
		return documentDate;
	}

	public void setDocumentDate(String documentDate) {
		this.documentDate = documentDate;
	}

	public double getDocumentAmount() {
		return documentAmount;
	}

	public void setDocumentAmount(double documentAmount) {
		this.documentAmount = documentAmount;
	}

}

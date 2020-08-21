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
public class DynamicDocumentFilledFormDetailsDTO {

	private String formElementName;

	private String value;

	public String getFormElementName() {
		return formElementName;
	}

	public void setFormElementName(String formElementName) {
		this.formElementName = formElementName;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}

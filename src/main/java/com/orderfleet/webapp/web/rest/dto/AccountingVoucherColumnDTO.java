package com.orderfleet.webapp.web.rest.dto;

import com.orderfleet.webapp.domain.DocumentAccountingVoucherColumn;

/**
 * A DTO AccountingVoucherColumn
 * 
 * @author Muhammed Riyas T
 * @since July 26, 2016
 */
public class AccountingVoucherColumnDTO {

	private String name;

	private boolean enabled;

	public AccountingVoucherColumnDTO() {

	}

	public AccountingVoucherColumnDTO(DocumentAccountingVoucherColumn documentAccountingVoucherColumn) {
		super();
		this.name = documentAccountingVoucherColumn.getAccountingVoucherColumn().getName();
		this.enabled = documentAccountingVoucherColumn.getEnabled();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

}

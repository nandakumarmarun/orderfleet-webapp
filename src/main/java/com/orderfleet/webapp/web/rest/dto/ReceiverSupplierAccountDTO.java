package com.orderfleet.webapp.web.rest.dto;

import java.util.List;

public class ReceiverSupplierAccountDTO {

	List<AccountProfileDTO> receivers;

	List<AccountProfileDTO> suppliers;

	public ReceiverSupplierAccountDTO() {

	}

	public ReceiverSupplierAccountDTO(List<AccountProfileDTO> receivers, List<AccountProfileDTO> suppliers) {
		super();
		this.receivers = receivers;
		this.suppliers = suppliers;
	}

	public List<AccountProfileDTO> getReceivers() {
		return receivers;
	}

	public void setReceivers(List<AccountProfileDTO> receivers) {
		this.receivers = receivers;
	}

	public List<AccountProfileDTO> getSuppliers() {
		return suppliers;
	}

	public void setSuppliers(List<AccountProfileDTO> suppliers) {
		this.suppliers = suppliers;
	}

}

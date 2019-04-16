package com.orderfleet.webapp.web.rest.dto;

import java.util.List;

/**
 * A SyncOperationDTO.
 *
 * @author Sarath
 * @since Mar 14, 2017
 */
public class SyncOperationDTO {

	private List<String> operationTypes;
	private String companyName;

	public SyncOperationDTO() {
		super();
	}

	public SyncOperationDTO(List<String> operationTypes, String companyName) {
		super();
		this.operationTypes = operationTypes;
		this.companyName = companyName;
	}

	public List<String> getOperationTypes() {
		return operationTypes;
	}

	public void setOperationTypes(List<String> operationTypes) {
		this.operationTypes = operationTypes;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

}

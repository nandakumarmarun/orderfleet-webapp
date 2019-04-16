package com.orderfleet.webapp.web.rest.dto;

import java.util.List;

/**
 * A FormFormElement DTO.
 * 
 * @author Muhammed Riyas T
 * @since July 22, 2016
 */
public class FormFormElementOrderDTO {

	private String formPid;

	List<FormElementOrderDTO> formElementOrderDTOs;

	public String getFormPid() {
		return formPid;
	}

	public void setFormPid(String formPid) {
		this.formPid = formPid;
	}

	public List<FormElementOrderDTO> getFormElementOrderDTOs() {
		return formElementOrderDTOs;
	}

	public void setFormElementOrderDTOs(List<FormElementOrderDTO> formElementOrderDTOs) {
		this.formElementOrderDTOs = formElementOrderDTOs;
	}

}

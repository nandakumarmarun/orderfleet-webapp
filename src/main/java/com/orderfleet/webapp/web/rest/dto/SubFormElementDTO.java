package com.orderfleet.webapp.web.rest.dto;

import com.orderfleet.webapp.domain.SubFormElement;

public class SubFormElementDTO {

	private String documentPid;
	
	private String formPid;
	
	private String formElementPid;
	
	private String formElementValueId;
	
	private String formElementValueName;
	
	private String subFormElementPid;

	
	public SubFormElementDTO(SubFormElement subFormElement) {
		this.documentPid = subFormElement.getDocument().getPid();
		this.formPid = subFormElement.getForm().getPid();
		this.formElementPid = subFormElement.getFormElement().getPid();
		this.formElementValueId = "Check dto";
		this.formElementValueName = subFormElement.getFormElementValue().getName();
		this.subFormElementPid = subFormElement.getSubFormElement().getPid();
	}
	public String getDocumentPid() {
		return documentPid;
	}

	public void setDocumentPid(String documentPid) {
		this.documentPid = documentPid;
	}

	public String getFormPid() {
		return formPid;
	}

	public void setFormPid(String formPid) {
		this.formPid = formPid;
	}

	public String getFormElementPid() {
		return formElementPid;
	}

	public void setFormElementPid(String formElementPid) {
		this.formElementPid = formElementPid;
	}

	public String getFormElementValueId() {
		return formElementValueId;
	}

	public void setFormElementValueId(String formElementValueId) {
		this.formElementValueId = formElementValueId;
	}

	public String getFormElementValueName() {
		return formElementValueName;
	}

	public void setFormElementValueName(String formElementValueName) {
		this.formElementValueName = formElementValueName;
	}

	public String getSubFormElementPid() {
		return subFormElementPid;
	}

	public void setSubFormElementPid(String subFormElementPid) {
		this.subFormElementPid = subFormElementPid;
	}
	
	
}

package com.orderfleet.webapp.web.rest.dto;

import com.orderfleet.webapp.domain.FilledFormDetail;

/**
 * A DTO for the FilledFormDetail entity.
 * 
 * @author Muhammed Riyas T
 * @since August 01, 2016
 */
public class FilledFormDetailDTO {

	private Long id;

	private String value;

	private String formElementPid;

	private String formElementName;

	private String formElementType;

	public FilledFormDetailDTO() {
		super();
	}

	public FilledFormDetailDTO(FilledFormDetail filledFormDetail) {
		super();
		this.id = filledFormDetail.getId();
		this.value = filledFormDetail.getValue() == null ? "" : filledFormDetail.getValue();
		this.formElementPid = filledFormDetail.getFormElement().getPid();
		this.formElementName = filledFormDetail.getFormElement().getName();
		this.formElementType = filledFormDetail.getFormElement().getFormElementType() == null ? "" :filledFormDetail.getFormElement().getFormElementType().getName();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getFormElementPid() {
		return formElementPid;
	}

	public void setFormElementPid(String formElementPid) {
		this.formElementPid = formElementPid;
	}

	public String getFormElementName() {
		return formElementName;
	}

	public void setFormElementName(String formElementName) {
		this.formElementName = formElementName;
	}

	public String getFormElementType() {
		return formElementType;
	}

	public void setFormElementType(String formElementType) {
		this.formElementType = formElementType;
	}

	@Override
	public String toString() {
		return "FilledFormDetailDTO [id=" + id + ", value=" + value + ", formElementPid=" + formElementPid
				+ ", formElementName=" + formElementName + ", formElementType=" + formElementType + "]";
	}

}

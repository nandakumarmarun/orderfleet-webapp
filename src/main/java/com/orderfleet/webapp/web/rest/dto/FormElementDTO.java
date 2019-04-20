package com.orderfleet.webapp.web.rest.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.orderfleet.webapp.domain.FormElement;
import com.orderfleet.webapp.domain.enums.LoadMobileData;

/**
 * A DTO for the FormElement entity.
 * 
 * @author Muhammed Riyas T
 * @since June 23, 2016
 */
public class FormElementDTO {

	private String pid;

	@NotNull
	@Size(min = 1, max = 1000)
	private String name;

	private Long formElementTypeId;

	private String formElementTypeName;

	private List<FormElementValueDTO> formElementValues;

	private boolean activated;

	private String defaultValue;

	private LocalDateTime lastModifiedDate;

	private boolean formLoadFromMobile;

	private LoadMobileData formLoadMobileData;

	public FormElementDTO() {
		super();
	}

	public FormElementDTO(FormElement formElement) {
		this.pid = formElement.getPid();
		this.name = formElement.getName();
		this.formElementTypeId = formElement.getFormElementType().getId();
		this.formElementTypeName = formElement.getFormElementType().getName();
		this.defaultValue = formElement.getDefaultValue();
		this.lastModifiedDate = formElement.getLastModifiedDate();
		this.formLoadFromMobile = formElement.getFormLoadFromMobile();
		this.formLoadMobileData = formElement.getFormLoadMobileData();
	}

	public FormElementDTO(String pid, String name) {
		this.pid = pid;
		this.name = name;
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getFormElementTypeId() {
		return formElementTypeId;
	}

	public void setFormElementTypeId(Long formElementTypeId) {
		this.formElementTypeId = formElementTypeId;
	}

	public String getFormElementTypeName() {
		return formElementTypeName;
	}

	public void setFormElementTypeName(String formElementTypeName) {
		this.formElementTypeName = formElementTypeName;
	}

	public List<FormElementValueDTO> getFormElementValues() {
		return formElementValues;
	}

	public void setFormElementValues(List<FormElementValueDTO> formElementValues) {
		this.formElementValues = formElementValues;
	}

	public boolean getActivated() {
		return activated;
	}

	public void setActivated(boolean activated) {
		this.activated = activated;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public LocalDateTime getLastModifiedDate() {
		return lastModifiedDate;
	}

	public void setLastModifiedDate(LocalDateTime lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}

	public boolean getFormLoadFromMobile() {
		return formLoadFromMobile;
	}

	public void setFormLoadFromMobile(boolean formLoadFromMobile) {
		this.formLoadFromMobile = formLoadFromMobile;
	}

	public LoadMobileData getFormLoadMobileData() {
		return formLoadMobileData;
	}

	public void setFormLoadMobileData(LoadMobileData formLoadMobileData) {
		this.formLoadMobileData = formLoadMobileData;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		FormElementDTO formElementDTO = (FormElementDTO) o;

		if (!Objects.equals(pid, formElementDTO.pid))
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(pid);
	}

	@Override
	public String toString() {
		return "FormElementDTO [pid=" + pid + ", name=" + name + ", formElementTypeId=" + formElementTypeId
				+ ", formElementTypeName=" + formElementTypeName + ", formElementValues=" + formElementValues
				+ ", activated=" + activated + "]";
	}
}

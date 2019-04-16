package com.orderfleet.webapp.web.rest.dto;

import java.util.Objects;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.orderfleet.webapp.domain.FormElementMaster;

/**
 * A DTO for the FormElementMaster entity.
 * 
 * @author Muhammed Riyas T
 * @since November 02, 2016
 */
public class FormElementMasterDTO {

	private String pid;

	@NotNull
	@Size(min = 1, max = 255)
	private String name;

	private String formElementMasterTypePid;

	private String formElementMasterTypeName;

	public FormElementMasterDTO() {
	}

	public FormElementMasterDTO(FormElementMaster formElementMaster) {
		super();
		this.pid = formElementMaster.getPid();
		this.name = formElementMaster.getName();
		this.formElementMasterTypePid = formElementMaster.getFormElementMasterType().getPid();
		this.formElementMasterTypeName = formElementMaster.getFormElementMasterType().getName();
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

	public String getFormElementMasterTypePid() {
		return formElementMasterTypePid;
	}

	public void setFormElementMasterTypePid(String formElementMasterTypePid) {
		this.formElementMasterTypePid = formElementMasterTypePid;
	}

	public String getFormElementMasterTypeName() {
		return formElementMasterTypeName;
	}

	public void setFormElementMasterTypeName(String formElementMasterTypeName) {
		this.formElementMasterTypeName = formElementMasterTypeName;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		FormElementMasterDTO formElementMasterDTO = (FormElementMasterDTO) o;

		if (!Objects.equals(pid, formElementMasterDTO.pid))
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(pid);
	}

	@Override
	public String toString() {
		return "FormElementMasterDTO{" + ", pid='" + pid + "'" + ", name='" + name + "'" + '}';
	}
}
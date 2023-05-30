package com.orderfleet.webapp.web.rest.dto;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.orderfleet.webapp.domain.FilledForm;

/**
 * A DTO for the FilledForm entity.
 * 
 * @author Sarath
 * @since Nov 4, 2016
 */
public class FilledFormDTO {

	private String pid;

	private String imageRefNo;

	private String formPid;

	private String formName;
	
	private boolean multipleRecord;

	private List<FilledFormDetailDTO> filledFormDetails;

	public FilledFormDTO() {
		super();
	}

	public FilledFormDTO(FilledForm filledForm) {
		super();
		this.pid = filledForm.getPid();
		this.imageRefNo = filledForm.getImageRefNo();
		if(filledForm.getForm() != null) {
			this.formPid = filledForm.getForm().getPid();
			this.formName = filledForm.getForm().getName();
		}
		this.filledFormDetails = filledForm.getFilledFormDetails().stream().map(FilledFormDetailDTO::new)
				.collect(Collectors.toList());
		if(filledForm.getForm() != null) {
			this.multipleRecord = filledForm.getForm().getMultipleRecord();
		}
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public String getImageRefNo() {
		return imageRefNo;
	}

	public void setImageRefNo(String imageRefNo) {
		this.imageRefNo = imageRefNo;
	}

	public String getFormPid() {
		return formPid;
	}

	public void setFormPid(String formPid) {
		this.formPid = formPid;
	}

	public String getFormName() {
		return formName;
	}

	public void setFormName(String formName) {
		this.formName = formName;
	}

	public List<FilledFormDetailDTO> getFilledFormDetails() {
		return filledFormDetails;
	}

	public void setFilledFormDetails(List<FilledFormDetailDTO> filledFormDetails) {
		this.filledFormDetails = filledFormDetails;
	}

	public boolean getMultipleRecord() {
		return multipleRecord;
	}

	public void setMultipleRecord(boolean multipleRecord) {
		this.multipleRecord = multipleRecord;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		FilledFormDTO formElementDTO = (FilledFormDTO) o;

		if (!Objects.equals(pid, formElementDTO.pid))
			return false;

		return true;
	}

	
	
	@Override
	public String toString() {
		return "FilledFormDTO [pid=" + pid + ", imageRefNo=" + imageRefNo + ", formPid=" + formPid + ", formName="
				+ formName + ", multipleRecord=" + multipleRecord + ", filledFormDetails=" + filledFormDetails + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(pid);
	}

}

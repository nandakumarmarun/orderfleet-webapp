package com.orderfleet.webapp.web.rest.dto;

import java.util.List;

public class FormFileDTO {

	private String formName;

	private List<FileDTO> files;

	public String getFormName() {
		return formName;
	}

	public void setFormName(String formName) {
		this.formName = formName;
	}

	public List<FileDTO> getFiles() {
		return files;
	}

	public void setFiles(List<FileDTO> files) {
		this.files = files;
	}

}

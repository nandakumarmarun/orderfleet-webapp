package com.orderfleet.webapp.web.rest.dto;

import java.time.LocalDateTime;
import java.util.Objects;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orderfleet.webapp.domain.Form;

/**
 * A DTO for the Form entity.
 * 
 * @author Muhammed Riyas T
 * @since June 21, 2016
 */
public class FormDTO {

	private String pid;

	@NotNull
	@Size(min = 1, max = 255)
	private String name;

	private String description;

	private String jsCode;

	private boolean multipleRecord;

	private Boolean activated;

	private LocalDateTime lastModifiedDate;

	public FormDTO() {
	}

	public FormDTO(Form form) {
		super();
		this.pid = form.getPid();
		this.name = form.getName();
		this.description = form.getDescription();
		this.jsCode = form.getJsCode();
		this.multipleRecord = form.getMultipleRecord();
		this.lastModifiedDate = form.getLastModifiedDate();
	}

	public FormDTO(String pid, String name) {
		this.pid = pid;
		this.name = name;
	}

	public Boolean getActivated() {
		return activated;
	}

	public void setActivated(Boolean activated) {
		this.activated = activated;
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getJsCode() {
		return jsCode;
	}

	public void setJsCode(String jsCode) {
		this.jsCode = jsCode;
	}

	public boolean getMultipleRecord() {
		return multipleRecord;
	}

	public void setMultipleRecord(boolean multipleRecord) {
		this.multipleRecord = multipleRecord;
	}

	public LocalDateTime getLastModifiedDate() {
		return lastModifiedDate;
	}

	public void setLastModifiedDate(LocalDateTime lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		FormDTO formDTO = (FormDTO) o;

		if (!Objects.equals(pid, formDTO.pid))
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(pid);
	}

	@Override
	public String toString() {
		String jsonString = "";
		try {
			ObjectMapper mapper = new ObjectMapper();
			jsonString = mapper.writeValueAsString(this);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return jsonString;
	}

}

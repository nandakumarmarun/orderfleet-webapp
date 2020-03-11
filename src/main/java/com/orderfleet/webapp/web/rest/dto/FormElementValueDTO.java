package com.orderfleet.webapp.web.rest.dto;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.orderfleet.webapp.domain.FormElement;
import com.orderfleet.webapp.domain.FormElementValue;

/**
 * A DTO for the FormElementValuesDTO entity.
 * 
 * @author Muhammed Riyas T
 * @since June 23, 2016
 */
public class FormElementValueDTO {

	private String id;

	@NotNull
	@Size(min = 1, max = 400)
	private String name;
	
	public FormElementValueDTO() {
		super();
	}

	public FormElementValueDTO(FormElementValue formElementValue) {
		this.id = String.valueOf(formElementValue.getId());
		this.name = formElementValue.getName();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		FormElementValueDTO fevDTO = (FormElementValueDTO) o;
		if (fevDTO.getName() == null || getName() == null) {
			return false;
		}
		return Objects.equals(getName(), fevDTO.getName());
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(getName());
	}

	@Override
	public String toString() {
		return "FormElementValueDTO{" + ", id='" + id + "'" + ", name='" + name + "'" + '}';
	}
}

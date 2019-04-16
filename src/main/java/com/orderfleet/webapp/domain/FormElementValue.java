package com.orderfleet.webapp.domain;

import javax.persistence.*;
import javax.validation.constraints.*;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import java.io.Serializable;
import java.util.Objects;

/**
 * A FormElementValue.
 * 
 * @author Shaheer
 * @since June 21, 2016
 */
@Entity
@Table(name = "tbl_form_element_values")
public class FormElementValue implements Serializable, Cloneable {

	private static final long serialVersionUID = 1L;

	@Id
	@GenericGenerator(name = "seq_form_element_values_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "seq_form_element_values_id") })
	@GeneratedValue(generator = "seq_form_element_values_id_GEN")
	@Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_form_element_values_id')")
	private Long id;

	@NotNull
	@Size(min = 1, max = 400)
	@Column(name = "name", length = 400, nullable = false)
	private String name;

	@NotNull
	@Column(name = "sort_order", nullable = false)
	private Integer sortOrder = 0;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "form_element_id")
	private FormElement formElement;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(Integer sortOrder) {
		this.sortOrder = sortOrder;
	}

	public FormElement getFormElement() {
		return formElement;
	}

	public void setFormElement(FormElement formElement) {
		this.formElement = formElement;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		FormElementValue formElementValue = (FormElementValue) o;
		if (formElementValue.id == null || id == null) {
			return false;
		}
		return Objects.equals(id, formElementValue.id);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(id);
	}

	@Override
	public String toString() {
		return "FormElementValue{" + "id=" + id + "'" + ", name='" + name + "'" + ", sortOrder='" + sortOrder + "'"
				+ '}';
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
}

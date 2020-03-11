package com.orderfleet.webapp.domain;

import javax.persistence.*;
import javax.validation.constraints.*;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.orderfleet.webapp.domain.enums.LoadMobileData;
import com.orderfleet.webapp.domain.enums.TallyDownloadStatus;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;

/**
 * A FormElement.
 * 
 * @author Shaheer
 * @since June 21, 2016
 */
@Entity
@Table(name = "tbl_form_element")
public class FormElement implements Serializable, Cloneable {

	private static final long serialVersionUID = 1L;

	@Id
	@GenericGenerator(name = "seq_form_element_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "seq_form_element_id") })
	@GeneratedValue(generator = "seq_form_element_id_GEN")
	@Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_form_element_id')")
	private Long id;

	@NotNull
	@Column(name = "pid", unique = true, nullable = false, updatable = false)
	private String pid;

	@NotNull
	@Size(min = 1, max = 400)
	@Column(name = "name", length = 1000, nullable = false)
	private String name;

	@NotNull
	@ManyToOne
	private FormElementType formElementType;

	@NotNull
	@ManyToOne
	private Company company;

	@OneToMany(fetch = FetchType.EAGER,cascade = { CascadeType.ALL })
	@JoinColumn(name = "form_element_id")
	@OrderBy("id")
	private Set<FormElementValue> formElementValues;

	@Column(name = "load_from_mobile", nullable = false, columnDefinition = "boolean DEFAULT 'FALSE'")
	private boolean formLoadFromMobile;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "load_mobile_data", nullable = false, columnDefinition = "character varying DEFAULT 'DEFAULT'")
	private LoadMobileData formLoadMobileData = LoadMobileData.DEFAULT;

	@NotNull
	@Column(name = "activated", nullable = false, columnDefinition = "boolean DEFAULT 'TRUE'")
	private boolean activated = true;

	@Column(name = "default_value", length = 1000)
	private String defaultValue;

	@Column(name = "created_date", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	@JsonIgnore
	private LocalDateTime createdDate = LocalDateTime.now();

	@Column(name = "last_modified_date")
	@JsonIgnore
	private LocalDateTime lastModifiedDate = LocalDateTime.now();

	@PreUpdate
	public void preUpdate() {
		this.lastModifiedDate = LocalDateTime.now();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public FormElementType getFormElementType() {
		return formElementType;
	}

	public void setFormElementType(FormElementType formElementType) {
		this.formElementType = formElementType;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public Set<FormElementValue> getFormElementValues() {
		return formElementValues;
	}

	public void setFormElementValues(Set<FormElementValue> formElementValues) {
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

	public LocalDateTime getCreatedDate() {
		return createdDate;
	}

	public LocalDateTime getLastModifiedDate() {
		return lastModifiedDate;
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
	public String toString() {
		return "FormElement [id=" + id + ", pid=" + pid + ", name=" + name + ", formElementType=" + formElementType
				+ ", company=" + company + ", formElementValues=" + formElementValues + ", formLoadFromMobile="
				+ formLoadFromMobile + ", formLoadMobileData=" + formLoadMobileData + ", activated=" + activated
				+ ", defaultValue=" + defaultValue + ", createdDate=" + createdDate + ", lastModifiedDate="
				+ lastModifiedDate + "]";
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		FormElement formElement = (FormElement) o;
		if (formElement.id == null || id == null) {
			return false;
		}
		return Objects.equals(id, formElement.id);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(id);
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
}

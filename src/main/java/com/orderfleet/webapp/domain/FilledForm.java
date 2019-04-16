package com.orderfleet.webapp.domain;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * A FilledForm.
 * 
 * @author Shaheer
 * @since June 21, 2016
 */
@Entity
@Table(name = "tbl_filled_form")
public class FilledForm implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GenericGenerator(name = "seq_filled_form_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "seq_filled_form_id") })
	@GeneratedValue(generator = "seq_filled_form_id_GEN")
	@Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_filled_form_id')")
	private Long id;

	@NotNull
	@Column(name = "pid", unique = true, nullable = false, updatable = false)
	private String pid;

	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "dynamic_document_header_id")
	private DynamicDocumentHeader dynamicDocumentHeader;

	@NotNull
	@Column(name = "imageRefNo", updatable = false)
	private String imageRefNo;

	@ManyToOne
	@NotNull
	private Form form;

	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name = "filled_form_id")
	@OrderBy("id")
	private List<FilledFormDetail> filledFormDetails;

	@JsonIgnore
	@ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinTable(name = "tbl_filled_form_file", joinColumns = {
			@JoinColumn(name = "filled_form_id", referencedColumnName = "id") }, inverseJoinColumns = {
					@JoinColumn(name = "file_id", referencedColumnName = "id") })
	private Set<File> files = new HashSet<>();

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

	public String getImageRefNo() {
		return imageRefNo;
	}

	public void setImageRefNo(String imageRefNo) {
		this.imageRefNo = imageRefNo;
	}

	public Form getForm() {
		return form;
	}

	public void setForm(Form form) {
		this.form = form;
	}

	public List<FilledFormDetail> getFilledFormDetails() {
		return filledFormDetails;
	}

	public void setFilledFormDetails(List<FilledFormDetail> filledFormDetails) {
		this.filledFormDetails = filledFormDetails;
	}

	public Set<File> getFiles() {
		return files;
	}

	public void setFiles(Set<File> files) {
		this.files = files;
	}

	public DynamicDocumentHeader getDynamicDocumentHeader() {
		return dynamicDocumentHeader;
	}

	public void setDynamicDocumentHeader(DynamicDocumentHeader dynamicDocumentHeader) {
		this.dynamicDocumentHeader = dynamicDocumentHeader;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		FilledForm filledForm = (FilledForm) o;
		if (filledForm.id == null || id == null) {
			return false;
		}
		return Objects.equals(id, filledForm.id);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(id);
	}

	@Override
	public String toString() {
		return "FilledForm{" + "id=" + id + ", pid='" + pid + "'" + '}';
	}
}

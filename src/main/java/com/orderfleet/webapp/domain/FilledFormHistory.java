package com.orderfleet.webapp.domain;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

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
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * A FilledFormHistory.
 * 
 * @author Muhammed Riyas T
 * @since October 24, 2016
 */
@Entity
@Table(name = "tbl_filled_form_history")
public class FilledFormHistory implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GenericGenerator(name = "seq_filled_form_history_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "seq_filled_form_history_id") })
	@GeneratedValue(generator = "seq_filled_form_history_id_GEN")
	@Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_filled_form_history_id')")
	private Long id;

	@NotNull
	@Column(name = "pid", nullable = false, updatable = false)
	private String pid;

	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "dynamic_document_header_history_id")
	private DynamicDocumentHeaderHistory dynamicDocumentHeaderHistory;

	@NotNull
	@Column(name = "imageRefNo", updatable = false)
	private String imageRefNo;

	@ManyToOne
	@NotNull
	private Form form;

	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name = "filled_form_history_id")
	private List<FilledFormDetailHistory> filledFormDetails;

	@JsonIgnore
	@ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinTable(name = "tbl_filled_form_file_history", joinColumns = {
			@JoinColumn(name = "filled_form_history_id", referencedColumnName = "id") }, inverseJoinColumns = {
					@JoinColumn(name = "file_id", referencedColumnName = "id") })
	private Set<File> files = new HashSet<>();

	public FilledFormHistory() {

	}

	public FilledFormHistory(FilledForm filledForm) {
		super();
		this.pid = filledForm.getPid();
		this.imageRefNo = filledForm.getImageRefNo();
		this.form = filledForm.getForm();
		this.filledFormDetails = filledForm.getFilledFormDetails().stream().map(FilledFormDetailHistory::new)
				.collect(Collectors.toList());
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

	public List<FilledFormDetailHistory> getFilledFormDetails() {
		return filledFormDetails;
	}

	public void setFilledFormDetails(List<FilledFormDetailHistory> filledFormDetails) {
		this.filledFormDetails = filledFormDetails;
	}

	public Set<File> getFiles() {
		return files;
	}

	public void setFiles(Set<File> files) {
		this.files = files;
	}

	public DynamicDocumentHeaderHistory getDynamicDocumentHeaderHistory() {
		return dynamicDocumentHeaderHistory;
	}

	public void setDynamicDocumentHeaderHistory(DynamicDocumentHeaderHistory dynamicDocumentHeaderHistory) {
		this.dynamicDocumentHeaderHistory = dynamicDocumentHeaderHistory;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		FilledFormHistory filledForm = (FilledFormHistory) o;
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

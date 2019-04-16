package com.orderfleet.webapp.domain;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.Parameter;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * A DynamicDocumentSettingsHeader.
 *
 * @author Sarath
 * @since Aug 28, 2017
 *
 */
@Entity
@Table(name = "tbl_dynamic_document_settings_header")
public class DynamicDocumentSettingsHeader implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GenericGenerator(name = "seq_dynamic_document_settings_header_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "seq_dynamic_document_settings_header_id") })
	@GeneratedValue(generator = "seq_dynamic_document_settings_header_id_GEN")
	@Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_dynamic_document_settings_header_id')")
	private Long id;

	@NotNull
	@Column(name = "pid", unique = true, nullable = false, updatable = false)
	private String pid;

	@NotNull
	@Column(name = "name", unique = true, nullable = false)
	private String name;

	@NotNull
	@Column(name = "title", nullable = false)
	private String title;

	@OneToMany(cascade = CascadeType.ALL)
	@LazyCollection(LazyCollectionOption.FALSE)
	@JoinColumn(name = "dynamic_document_settings_header_id")
	private List<DynamicDocumentSettingsColumns> documentSettingsColumns;

	@OneToMany(cascade = CascadeType.ALL)
	@LazyCollection(LazyCollectionOption.FALSE)
	@JoinColumn(name = "dynamic_document_settings_header_id")
	private List<DynamicDocumentSettingsRowColour> documentSettingsRowColours;

	@OneToMany(cascade = CascadeType.ALL)
	@LazyCollection(LazyCollectionOption.FALSE)
	@JoinColumn(name = "dynamic_document_settings_header_id")
	private List<DynamicDocumentReportDetail> dynamicDocumentReportDetails;

	@NotNull
	@ManyToOne
	private Document document;

	@ManyToOne
	@NotNull
	private Company company;

	@Column(name = "created_date", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	@JsonIgnore
	private LocalDateTime createdDate = LocalDateTime.now();

	@Column(name = "last_modified_date", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	@JsonIgnore
	private LocalDateTime lastModifiedDate = LocalDateTime.now();

	@PreUpdate
	public void preUpdate() {
		this.lastModifiedDate = LocalDateTime.now();
	}

	public DynamicDocumentSettingsHeader() {
		super();
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

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public List<DynamicDocumentSettingsColumns> getDocumentSettingsColumns() {
		return documentSettingsColumns;
	}

	public void setDocumentSettingsColumns(List<DynamicDocumentSettingsColumns> documentSettingsColumns) {
		this.documentSettingsColumns = documentSettingsColumns;
	}

	public Document getDocument() {
		return document;
	}

	public void setDocument(Document document) {
		this.document = document;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public List<DynamicDocumentSettingsRowColour> getDocumentSettingsRowColours() {
		return documentSettingsRowColours;
	}

	public void setDocumentSettingsRowColours(List<DynamicDocumentSettingsRowColour> documentSettingsRowColours) {
		this.documentSettingsRowColours = documentSettingsRowColours;
	}

	public List<DynamicDocumentReportDetail> getDynamicDocumentReportDetails() {
		return dynamicDocumentReportDetails;
	}

	public void setDynamicDocumentReportDetails(List<DynamicDocumentReportDetail> dynamicDocumentReportDetails) {
		this.dynamicDocumentReportDetails = dynamicDocumentReportDetails;
	}

	@Override
	public String toString() {
		return "DynamicDocumentSettingsHeader [id=" + id + ", pid=" + pid + ", name=" + name + ", title=" + title
				+ ", documentSettingsColumns=" + documentSettingsColumns + ",document=" + document + ", company="
				+ company + ", createdDate=" + createdDate + ", lastModifiedDate=" + lastModifiedDate + "]";
	}

}

package com.orderfleet.webapp.domain;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

/**
 * A DynamicDocumentHeaderHistory.
 * 
 * @author Muhammed Riyas T
 * @since October 24, 2016
 */
@Entity
@Table(name = "tbl_dynamic_document_header_history")
public class DynamicDocumentHeaderHistory implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GenericGenerator(name = "seq_dynamic_document_header_history_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "seq_dynamic_document_header_history_id") })
	@GeneratedValue(generator = "seq_dynamic_document_header_history_id_GEN")
	@Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_dynamic_document_header_history_id')")
	private Long id;

	@NotNull
	@Column(name = "pid", nullable = false, updatable = false)
	private String pid;

	@NotNull
	@Column(name = "document_number_local", nullable = false, updatable = false)
	private String documentNumberLocal;

	@NotNull
	@Column(name = "document_number_server", nullable = false, updatable = false)
	private String documentNumberServer;

	@CreatedDate
	@Column(name = "created_date", nullable = false)
	private LocalDateTime createdDate = LocalDateTime.now();

	@LastModifiedDate
	@Column(name = "updated_date", nullable = false)
	private LocalDateTime updatedDate = LocalDateTime.now();

	@NotNull
	@Column(name = "document_date", nullable = false)
	private LocalDateTime documentDate;

	@NotNull
	@ManyToOne
	private Document document;

	@NotNull
	@ManyToOne
	private User createdBy;

	@ManyToOne
	private User updatedBy;

	@ManyToOne
	private EmployeeProfile employee;

	@NotNull
	@ManyToOne
	private ExecutiveTaskExecution executiveTaskExecution;

	@ManyToOne
	@NotNull
	private Company company;

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "dynamic_document_header_history_id")
	private List<FilledFormHistory> filledForms;

	public DynamicDocumentHeaderHistory() {

	}

	public DynamicDocumentHeaderHistory(DynamicDocumentHeader dynamicDocumentHeader) {
		super();
		this.pid = dynamicDocumentHeader.getPid();
		this.documentNumberLocal = dynamicDocumentHeader.getDocumentNumberLocal();
		this.documentNumberServer = dynamicDocumentHeader.getDocumentNumberServer();
		this.createdDate = dynamicDocumentHeader.getCreatedDate();
		this.updatedDate = dynamicDocumentHeader.getUpdatedDate();
		this.documentDate = dynamicDocumentHeader.getDocumentDate();
		this.document = dynamicDocumentHeader.getDocument();
		this.createdBy = dynamicDocumentHeader.getCreatedBy();
		this.updatedBy = dynamicDocumentHeader.getUpdatedBy();
		this.employee = dynamicDocumentHeader.getEmployee();
		this.executiveTaskExecution = dynamicDocumentHeader.getExecutiveTaskExecution();
		this.company = dynamicDocumentHeader.getCompany();
		this.filledForms = dynamicDocumentHeader.getFilledForms().stream().map(FilledFormHistory::new)
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

	public String getDocumentNumberLocal() {
		return documentNumberLocal;
	}

	public void setDocumentNumberLocal(String documentNumberLocal) {
		this.documentNumberLocal = documentNumberLocal;
	}

	public String getDocumentNumberServer() {
		return documentNumberServer;
	}

	public void setDocumentNumberServer(String documentNumberServer) {
		this.documentNumberServer = documentNumberServer;
	}

	public LocalDateTime getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(LocalDateTime createdDate) {
		this.createdDate = createdDate;
	}

	public LocalDateTime getUpdatedDate() {
		return updatedDate;
	}

	public void setUpdatedDate(LocalDateTime updatedDate) {
		this.updatedDate = updatedDate;
	}

	public LocalDateTime getDocumentDate() {
		return documentDate;
	}

	public void setDocumentDate(LocalDateTime documentDate) {
		this.documentDate = documentDate;
	}

	public Document getDocument() {
		return document;
	}

	public void setDocument(Document document) {
		this.document = document;
	}

	public User getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(User createdBy) {
		this.createdBy = createdBy;
	}

	public User getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(User updatedBy) {
		this.updatedBy = updatedBy;
	}

	public EmployeeProfile getEmployee() {
		return employee;
	}

	public void setEmployee(EmployeeProfile employee) {
		this.employee = employee;
	}

	public ExecutiveTaskExecution getExecutiveTaskExecution() {
		return executiveTaskExecution;
	}

	public void setExecutiveTaskExecution(ExecutiveTaskExecution executiveTaskExecution) {
		this.executiveTaskExecution = executiveTaskExecution;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public List<FilledFormHistory> getFilledForms() {
		return filledForms;
	}

	public void setFilledForms(List<FilledFormHistory> filledForms) {
		this.filledForms = filledForms;
	}

}

package com.orderfleet.webapp.domain;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

/**
 * A TaskReferenceDocument.
 * 
 * @author Muhammed Riyas T
 * @since October 17, 2016
 */
@Entity
@Table(name = "tbl_task_reference_document")
public class TaskReferenceDocument implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GenericGenerator(name = "seq_task_reference_document_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "seq_task_reference_document_id") })
	@GeneratedValue(generator = "seq_task_reference_document_id_GEN")
	@Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_task_reference_document_id')")
	private Long id;

	@NotNull
	@Column(name = "pid", unique = true, nullable = false, updatable = false)
	private String pid;

	@NotNull
	@ManyToOne
	private Task task;

	@NotNull
	@ManyToOne
	private ExecutiveTaskExecution executiveTaskExecution;

	// inventory/accounting voucher header pid
	@Column(name = "ref_transaction_pid")
	private String refTransactionPid;

	@Column(name = "ref_trans_document_number")
	private String refTransDocumentNumber;

	@NotNull
	@ManyToOne
	private Document refDocument;

	@NotNull
	@ManyToOne
	private Company company;

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

	public Task getTask() {
		return task;
	}

	public void setTask(Task task) {
		this.task = task;
	}

	public ExecutiveTaskExecution getExecutiveTaskExecution() {
		return executiveTaskExecution;
	}

	public void setExecutiveTaskExecution(ExecutiveTaskExecution executiveTaskExecution) {
		this.executiveTaskExecution = executiveTaskExecution;
	}

	public String getRefTransactionPid() {
		return refTransactionPid;
	}

	public void setRefTransactionPid(String refTransactionPid) {
		this.refTransactionPid = refTransactionPid;
	}

	public String getRefTransDocumentNumber() {
		return refTransDocumentNumber;
	}

	public void setRefTransDocumentNumber(String refTransDocumentNumber) {
		this.refTransDocumentNumber = refTransDocumentNumber;
	}

	public Document getRefDocument() {
		return refDocument;
	}

	public void setRefDocument(Document refDocument) {
		this.refDocument = refDocument;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		TaskReferenceDocument taskReferenceDocument = (TaskReferenceDocument) o;
		if (taskReferenceDocument.id == null || id == null) {
			return false;
		}
		return Objects.equals(id, taskReferenceDocument.id);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(id);
	}

}

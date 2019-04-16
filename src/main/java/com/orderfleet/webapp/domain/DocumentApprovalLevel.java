package com.orderfleet.webapp.domain;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

/**
 * A DocumentApprovalLevel.
 * 
 * @author Muhammed Riyas T
 * @since November 19, 2016
 */
@Entity
@Table(name = "tbl_document_approval_level")
public class DocumentApprovalLevel implements Serializable, Cloneable {

	private static final long serialVersionUID = 1L;

	@Id
	@GenericGenerator(name = "seq_document_approval_level_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "seq_document_approval_level_id") })
	@GeneratedValue(generator = "seq_document_approval_level_id_GEN")
	@Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_document_approval_level_id')")
	private Long id;

	@NotNull
	@Column(name = "pid", unique = true, nullable = false, updatable = false)
	private String pid;

	@NotNull
	@Size(min = 1, max = 255)
	@Column(name = "name", length = 255, nullable = false)
	private String name;

	@Column(name = "approval_order")
	private int approvalOrder;

	@Column(name = "approver_count")
	private int approverCount;

	@Column(name = "script", length = 15000)
	private String script;

	@Column(name = "required")
	private boolean required;

	@NotNull
	@ManyToOne
	private Document document;

	@ManyToOne
	private Company company;

	@ManyToMany
	@JoinTable(name = "tbl_document_approval_level_users", joinColumns = @JoinColumn(name = "document_approval_level_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"))
	private Set<User> users = new HashSet<>();

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

	public boolean getRequired() {
		return required;
	}

	public void setRequired(boolean required) {
		this.required = required;
	}

	public int getApprovalOrder() {
		return approvalOrder;
	}

	public void setApprovalOrder(int approvalOrder) {
		this.approvalOrder = approvalOrder;
	}

	public int getApproverCount() {
		return approverCount;
	}

	public void setApproverCount(int approverCount) {
		this.approverCount = approverCount;
	}

	public String getScript() {
		return script;
	}

	public void setScript(String script) {
		this.script = script;
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

	public Set<User> getUsers() {
		return users;
	}

	public void setUsers(Set<User> users) {
		this.users = users;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		DocumentApprovalLevel documentApprovalLevel = (DocumentApprovalLevel) o;
		if (documentApprovalLevel.id == null || id == null) {
			return false;
		}
		return Objects.equals(id, documentApprovalLevel.id);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(id);
	}

	@Override
	public String toString() {
		return "DocumentApprovalLevel{" + ", id='" + id + "'" + ", pid='" + pid + "'" + ", name='" + name + "'"
				+ ", required='" + required + "'" + ", approvalOrder='" + approvalOrder + "'" + '}';
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

}

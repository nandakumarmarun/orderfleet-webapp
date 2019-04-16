package com.orderfleet.webapp.domain;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.orderfleet.webapp.domain.enums.AccountTypeColumn;

/**
 * A DocumentAccountType
 * 
 * @author Muhammed Riyas T
 * @since August 11, 2016
 */
@Entity
@Table(name = "tbl_document_account_type")
public class DocumentAccountType implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GenericGenerator(name = "seq_document_account_type_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "seq_document_account_type_id") })
	@GeneratedValue(generator = "seq_document_account_type_id_GEN")
	@Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_document_account_type_id')")
	private Long id;

	@NotNull
	@ManyToOne
	private Document document;

	@NotNull
	@ManyToOne
	private AccountType accountType;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "account_type_column", nullable = false)
	private AccountTypeColumn accountTypeColumn;

	@ManyToOne()
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

	public DocumentAccountType() {
		super();
	}

	public DocumentAccountType(Document document, AccountType accountType, AccountTypeColumn accountTypeColumn,
			Company company) {
		super();
		this.document = document;
		this.accountType = accountType;
		this.accountTypeColumn = accountTypeColumn;
		this.company = company;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Document getDocument() {
		return document;
	}

	public void setDocument(Document document) {
		this.document = document;
	}

	public AccountType getAccountType() {
		return accountType;
	}

	public void setAccountType(AccountType accountType) {
		this.accountType = accountType;
	}

	public AccountTypeColumn getAccountTypeColumn() {
		return accountTypeColumn;
	}

	public void setAccountTypeColumn(AccountTypeColumn accountTypeColumn) {
		this.accountTypeColumn = accountTypeColumn;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public LocalDateTime getCreatedDate() {
		return createdDate;
	}

	public LocalDateTime getLastModifiedDate() {
		return lastModifiedDate;
	}

}

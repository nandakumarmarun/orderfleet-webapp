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
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

/**
 * A DocumentPrintEmail
 * 
 * @author Fahad
 * @since Apr 19, 2017
 */

@Entity
@Table(name = "tbl_document_print_email")
public class DocumentPrintEmail implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Id
	@GenericGenerator(name = "seq_document_print_email_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "seq_document_print_email_id") })
	@GeneratedValue(generator = "seq_document_print_email_id_GEN")
	@Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_document_print_email_id')")
	private Long id;
	
	@NotNull
	@Size(min = 1, max = 255)
	@Column(name = "name", length = 255, nullable = false)
	private String name;
	
	@ManyToOne
	@NotNull
	private Company company;
	
	@NotNull
	@ManyToOne
	private Document document;
	
	@Column(name = "print_file_path")
	private String printFilePath;
	
	@Column(name = "email_file_path")
	private String emailFilePath;

	public DocumentPrintEmail() {
		super();
	}
	
	
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


	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public Document getDocument() {
		return document;
	}

	public void setDocument(Document document) {
		this.document = document;
	}

	public String getPrintFilePath() {
		return printFilePath;
	}

	public void setPrintFilePath(String printFilePath) {
		this.printFilePath = printFilePath;
	}

	public String getEmailFilePath() {
		return emailFilePath;
	}

	public void setEmailFilePath(String emailFilePath) {
		this.emailFilePath = emailFilePath;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		DocumentPrintEmail documentPrintEmail = (DocumentPrintEmail) o;
		if (documentPrintEmail.id == null || id == null) {
			return false;
		}
		return Objects.equals(id, documentPrintEmail.id);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(id);
	}


	@Override
	public String toString() {
		return "DocumentPrintEmail [id=" + id + ", name=" + name + ", company=" + company + ", document=" + document
				+ ", printFilePath=" + printFilePath + ", emailFilePath=" + emailFilePath + "]";
	}
	
	
	
	
}

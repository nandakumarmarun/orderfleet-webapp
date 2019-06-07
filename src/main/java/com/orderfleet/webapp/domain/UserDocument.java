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
 * A UserDocument
 * 
 * @author Sarath
 * @since July 5, 2016
 */
@Entity
@Table(name = "tbl_user_document")
public class UserDocument implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GenericGenerator(name = "seq_user_document_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "seq_user_document_id") })
	@GeneratedValue(generator = "seq_user_document_id_GEN")
	@Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_user_document_id')")
	private Long id;

	@NotNull
	@ManyToOne
	private User user;

	@NotNull
	@ManyToOne
	private Document document;

	@NotNull
	@ManyToOne
	private Company company;

	@Column(name = "image_option", nullable = false, columnDefinition = "boolean DEFAULT 'FALSE'")
	private boolean imageOption;

	@Column(name = "sms_option", nullable = false, columnDefinition = "boolean DEFAULT 'FALSE'")
	private boolean smsOption;

	public UserDocument() {
		super();
	}

	public UserDocument(User user, Document document, Company company, boolean imageOption, boolean smsOption) {
		super();
		this.user = user;
		this.document = document;
		this.company = company;
		this.imageOption = imageOption;
		this.smsOption = smsOption;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
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

	public boolean getImageOption() {
		return imageOption;
	}

	public void setImageOption(boolean imageOption) {
		this.imageOption = imageOption;
	}

	public boolean getSmsOption() {
		return smsOption;
	}

	public void setSmsOption(boolean smsOption) {
		this.smsOption = smsOption;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	@Override
	public String toString() {
		return "UserDocument [id=" + id + ", user=" + user + ", document=" + document + "]";
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		UserDocument userDocument = (UserDocument) o;
		if (userDocument.id == null || id == null) {
			return false;
		}
		return Objects.equals(id, userDocument.id);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(id);
	}

}

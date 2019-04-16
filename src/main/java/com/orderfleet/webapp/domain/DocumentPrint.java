package com.orderfleet.webapp.domain;

import java.io.Serializable;

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
 * A Document Print. created to set mobile print option.
 *
 * @author Sarath
 * @since Aug 12, 2017
 *
 */
@Entity
@Table(name = "tbl_document_print")
public class DocumentPrint implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GenericGenerator(name = "seq_document_print_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "seq_document_print_id") })
	@GeneratedValue(generator = "seq_document_print_id_GEN")
	@Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_document_print_id')")
	private Long id;

	@NotNull
	@Column(name = "pid", unique = true, nullable = false, updatable = false)
	private String pid;

	@ManyToOne
	@NotNull
	private User user;

	@ManyToOne
	@NotNull
	private Activity activity;

	@ManyToOne
	@NotNull
	private Document document;

	@ManyToOne
	@NotNull
	private Company company;

	@NotNull
	@Column(name = "print_Status", nullable = false)
	private boolean printStatus;

	public DocumentPrint() {
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

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Activity getActivity() {
		return activity;
	}

	public void setActivity(Activity activity) {
		this.activity = activity;
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

	public boolean isPrintStatus() {
		return printStatus;
	}

	public void setPrintStatus(boolean printStatus) {
		this.printStatus = printStatus;
	}

	@Override
	public String toString() {
		return "DocumentPrint [id=" + id + ", pid=" + pid + ", user=" + user.getLogin() + ", activity="
				+ activity.getName() + ", document=" + document.getName() + ", company=" + company.getLegalName()
				+ ", printStatus=" + printStatus + "]";
	}

}

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
 * A UserPriceLevel.
 * 
 * @author Muhammed Riyas T
 * @since October 05, 2016
 */
@Entity
@Table(name = "tbl_user_knowledgebase_files")
public class UserKnowledgebaseFile implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Id
	@GenericGenerator(name = "seq_user_knowledgebase_files_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "seq_user_knowledgebase_files_id") })
	@GeneratedValue(generator = "seq_user_knowledgebase_files_id_GEN")
	@Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_user_knowledgebase_files_id')")
	private Long id;

	@NotNull
	@ManyToOne
	private User user;

	@NotNull
	@ManyToOne
	private KnowledgebaseFiles knowledgebaseFiles;

	@NotNull
	@ManyToOne
	private Company company;

	public UserKnowledgebaseFile() {
		super();
	}

	public UserKnowledgebaseFile(User user, KnowledgebaseFiles knowledgebaseFiles, Company company) {
		super();
		this.user = user;
		this.knowledgebaseFiles = knowledgebaseFiles;
		this.company = company;
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

	public KnowledgebaseFiles getKnowledgebaseFiles() {
		return knowledgebaseFiles;
	}

	public void setKnowledgebaseFiles(KnowledgebaseFiles knowledgebaseFiles) {
		this.knowledgebaseFiles = knowledgebaseFiles;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

}

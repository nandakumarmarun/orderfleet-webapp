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
 * A KnowledgeBaseFiles.
 * 
 * @author Sarath
 * @since Aug 10, 2016
 */
@Entity
@Table(name = "tbl_knowledgebase_files")
public class KnowledgebaseFiles implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GenericGenerator(name = "seq_knowledgebase_files_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "seq_knowledgebase_files_id") })
	@GeneratedValue(generator = "seq_knowledgebase_files_id_GEN")
	@Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_knowledgebase_files_id')")
	private Long id;

	@Column(name = "file_name", length = 255, nullable = false, columnDefinition = "character varying DEFAULT 'Test'")
	private String fileName;

	@NotNull
	@Column(name = "pid", unique = true, nullable = false, updatable = false)
	private String pid;

	@Column(name = "search_tags", length = 3000)
	private String searchTags;

	@ManyToOne
	@NotNull
	private File file;

	@ManyToOne
	@NotNull
	private Knowledgebase knowledgebase;

	@NotNull
	@ManyToOne
	private Company company;

	public KnowledgebaseFiles() {
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

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getSearchTags() {
		return searchTags;
	}

	public void setSearchTags(String searchTags) {
		this.searchTags = searchTags;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public Knowledgebase getKnowledgebase() {
		return knowledgebase;
	}

	public void setKnowledgebase(Knowledgebase knowledgebase) {
		this.knowledgebase = knowledgebase;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

}

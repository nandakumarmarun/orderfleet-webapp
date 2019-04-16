package com.orderfleet.webapp.domain;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

/**
 * A File.
 * 
 * @author Shaheer
 * @since August 01, 2016
 */
@Entity
@Table(name = "tbl_file")
public class File implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GenericGenerator(name = "seq_file_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "seq_file_id") })
	@GeneratedValue(generator = "seq_file_id_GEN")
	@Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_file_id')")
	private Long id;

	@NotNull
	@Column(name = "pid", unique = true, nullable = false, updatable = false)
	private String pid;

	@ManyToOne
	@JoinColumn(name = "persistent_file_id", referencedColumnName = "id", nullable = true)
	private PersistentFile persistentFile;

	@Column(name = "file_name", length = 255, nullable = false)
	private String fileName;

	@Column(name = "mime_type", length = 100, nullable = false)
	private String mimeType;

	@Column(name = "uploaded_date", nullable = false, updatable = false)
	private LocalDateTime uploadedDate;

	@Column(name = "description", length = 500)
	private String description;

	public File() {
		super();
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public PersistentFile getPersistentFile() {
		return persistentFile;
	}

	public void setPersistentFile(PersistentFile persistentFile) {
		this.persistentFile = persistentFile;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public LocalDateTime getUploadedDate() {
		return uploadedDate;
	}

	public void setUploadedDate(LocalDateTime uploadedDate) {
		this.uploadedDate = uploadedDate;
	}

}
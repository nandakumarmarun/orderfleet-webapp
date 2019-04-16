package com.orderfleet.webapp.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

/**
 * A PersistentFile.
 * 
 * @author Shaheer
 * @since August 01, 2016
 */
@Entity
@Table(name = "tbl_persistent_file")
public class PersistentFile {
	
	@Id
	@GenericGenerator(name = "seq_persistent_file_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "seq_persistent_file_id") })
	@GeneratedValue(generator = "seq_persistent_file_id_GEN")
	@Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_persistent_file_id')")
	private Long id;
	
	@Column(name = "location", length = 255, nullable = false)
	private String location;
	
	@Column(name = "md5", length = 255, nullable = false, unique=true)
	private String md5;
	
	public PersistentFile() {
		super();
	}
	
	public PersistentFile(String location, String md5) {
		super();
		this.location = location;
		this.md5 = md5;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getMd5() {
		return md5;
	}

	public void setMd5(String md5) {
		this.md5 = md5;
	}

}
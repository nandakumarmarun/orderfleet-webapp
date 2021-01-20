package com.orderfleet.webapp.domain;

import java.io.Serializable;
import java.util.Arrays;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

@Entity
@Table(name = "tbl_best_performer")
public class BestPerformer implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GenericGenerator(name = "seq_best_performer_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "seq_best_performer_id") })
	@GeneratedValue(generator = "seq_best_performer_id_GEN")
	@Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_best_performer_id')")
	private Long id;
	
	@ManyToOne
	private Company company;
	
	@NotNull
	@Column(name = "pid", unique = true, nullable = false, updatable = false)
	private String pid;
	
	@Size(max = 5000000)
	@Lob
	@Column(name = "logo")
	private byte[] logo;

	@Column(name = "logo_content_type", length = 255)
	private String logoContentType;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public byte[] getLogo() {
		return logo;
	}

	public void setLogo(byte[] logo) {
		this.logo = logo;
	}

	public String getLogoContentType() {
		return logoContentType;
	}

	public void setLogoContentType(String logoContentType) {
		this.logoContentType = logoContentType;
	}

	@Override
	public String toString() {
		return "BestPerformer [id=" + id + ", pid=" + pid + ", logoContentType=" + logoContentType + "]";
	}	
}

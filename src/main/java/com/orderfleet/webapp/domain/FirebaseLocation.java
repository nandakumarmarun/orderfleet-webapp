package com.orderfleet.webapp.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

@Entity
@Table(name = "tbl_firebase_location")
public class FirebaseLocation {

	
	private static final long serialVersionUID = 1L;

	@Id
	@GenericGenerator(name = "seq_account_group_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "seq_account_group_id") })
	@GeneratedValue(generator = "seq_account_group_id_GEN")
	@Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_account_group_id')")
	private Long id;

	@NotNull
	@Column(name = "pid", unique = true, nullable = false, updatable = false)
	private String pid;
	private String key;

	private String companyName;

	private LocalDateTime date;

	private BigDecimal latitude;

	private BigDecimal longitude;

	public FirebaseLocation() {
		super();
		// TODO Auto-generated constructor stub
	}

	public FirebaseLocation(String key, String companyName, LocalDateTime date, BigDecimal latitude,
			BigDecimal longitude) {
		super();
		this.key = key;
		this.companyName = companyName;
		this.date = date;
		this.latitude = latitude;
		this.longitude = longitude;
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

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public LocalDateTime getDate() {
		return date;
	}

	public void setDate(LocalDateTime date) {
		this.date = date;
	}

	public BigDecimal getLatitude() {
		return latitude;
	}

	public void setLatitude(BigDecimal latitude) {
		this.latitude = latitude;
	}

	public BigDecimal getLongitude() {
		return longitude;
	}

	public void setLongitude(BigDecimal longitude) {
		this.longitude = longitude;
	}
	
	

	
}

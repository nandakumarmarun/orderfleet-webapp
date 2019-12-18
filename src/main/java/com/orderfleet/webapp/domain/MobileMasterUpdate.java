package com.orderfleet.webapp.domain;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

@Entity
@Table(name = "tbl_mobile_master_update")
public class MobileMasterUpdate implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GenericGenerator(name = "seq_mobile_master_update_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "seq_mobile_master_update_id") })
	@GeneratedValue(generator = "seq_mobile_master_update_id_GEN")
	@Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_mobile_master_update_id')")
	private Long id;

	@NotNull
	@Column(name = "pid", unique = true, nullable = false, updatable = false)
	private String pid;

	@ManyToOne
	@NotNull
	private User user;

	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name = "mobile_master_update_id")
	@Fetch(value = FetchMode.SUBSELECT)
	private List<MobileMasterDetail> mobileMasterDetails;

	@Column(name = "update_time")
	private String updateTime;

	@NotNull
	@Column(name = "created_date", nullable = false)
	private LocalDateTime createdDate = LocalDateTime.now();

	@ManyToOne
	@NotNull
	private Company company;

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

	public LocalDateTime getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(LocalDateTime createdDate) {
		this.createdDate = createdDate;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public List<MobileMasterDetail> getMobileMasterDetails() {
		return mobileMasterDetails;
	}

	public void setMobileMasterDetails(List<MobileMasterDetail> mobileMasterDetails) {
		this.mobileMasterDetails = mobileMasterDetails;
	}

	public String getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

}

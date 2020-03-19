package com.orderfleet.webapp.domain;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import com.orderfleet.webapp.domain.enums.MobileMasterItem;

@Entity
@Table(name = "tbl_mobile_master_detail")
public class MobileMasterDetail implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GenericGenerator(name = "seq_mobile_master_detail_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "seq_mobile_master_detail_id") })
	@GeneratedValue(generator = "seq_mobile_master_detail_id_GEN")
	@Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_mobile_master_detail_id')")
	private Long id;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "mobile_master_item", nullable = false, columnDefinition = "character varying DEFAULT 'DEFAULT'")
	private MobileMasterItem mobileMasterItem;

	//(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@ManyToOne
	@JoinColumn(name = "mobile_master_update_id")
	private MobileMasterUpdate mobileMasterUpdate;

	@Column(name = "operation_time")
	private String operationTime;

	@Column(name = "count")
	private Long count;

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

	public MobileMasterItem getMobileMasterItem() {
		return mobileMasterItem;
	}

	public void setMobileMasterItem(MobileMasterItem mobileMasterItem) {
		this.mobileMasterItem = mobileMasterItem;
	}

	public MobileMasterUpdate getMobileMasterUpdate() {
		return mobileMasterUpdate;
	}

	public void setMobileMasterUpdate(MobileMasterUpdate mobileMasterUpdate) {
		this.mobileMasterUpdate = mobileMasterUpdate;
	}

	public String getOperationTime() {
		return operationTime;
	}

	public void setOperationTime(String operationTime) {
		this.operationTime = operationTime;
	}

	public Long getCount() {
		return count;
	}

	public void setCount(Long count) {
		this.count = count;
	}
	
	public LocalDateTime getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(LocalDateTime createdDate) {
		this.createdDate = createdDate;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	
}

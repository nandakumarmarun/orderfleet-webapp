package com.orderfleet.webapp.domain;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.springframework.data.annotation.CreatedDate;

@Entity
@Table(name = "tbl_dynamic_report_header")
public class DynamicReportHeader implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GenericGenerator(name = "seq_dynamic_report_header_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "seq_dynamic_report_header_id") })
	@GeneratedValue(generator = "seq_dynamic_report_header_id_GEN")
	@Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_dynamic_report_header_id')")
	private Long id;
	
	@ManyToOne
	@JoinColumn(name = "dynamic_report_name_id")
	private DynamicReportName dynamicReportName;
	
	@CreatedDate
	@Column(name = "created_date", nullable = false)
	private LocalDateTime createdDate = LocalDateTime.now();
	
	@OneToMany( cascade = CascadeType.ALL)
	@JoinColumn(name = "dynamic_report_header_id")
	private List<DynamicReportDetail> dynamicReportDetails;
	
	@NotNull
	@ManyToOne
	private User createdBy;
	
	@NotNull
	private int columnCount;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public int getColumnCount() {
		return columnCount;
	}

	public void setColumnCount(int columnCount) {
		this.columnCount = columnCount;
	}
	
	public DynamicReportName getDynamicReportName() {
		return dynamicReportName;
	}

	public void setDynamicReportName(DynamicReportName dynamicReportName) {
		this.dynamicReportName = dynamicReportName;
	}

	public LocalDateTime getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(LocalDateTime createdDate) {
		this.createdDate = createdDate;
	}

	public User getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(User createdBy) {
		this.createdBy = createdBy;
	}
	
	public List<DynamicReportDetail> getDynamicReportDetails() {
		return dynamicReportDetails;
	}

	public void setDynamicReportDetails(List<DynamicReportDetail> dynamicReportDetails) {
		this.dynamicReportDetails = dynamicReportDetails;
	}
	
}

package com.orderfleet.webapp.domain;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.orderfleet.webapp.domain.enums.ApprovalStatus;

@Entity
@Table(name = "tbl_root_plan_detail")
public class RootPlanDetail implements Serializable{

	private static final long serialVersionUID = 1L;

	@Id
	@GenericGenerator(name = "seq_root_plan_detail_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "seq_root_plan_detail_id") })
	@GeneratedValue(generator = "seq_root_plan_detail_id_GEN")
	@Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_root_plan_detail_id')")
	private Long id;
	
	@NotNull
	@Column(name = "pid", unique = true, nullable = false, updatable = false)
	private String pid;
	
	@NotNull
	@ManyToOne
	private Company company;
	
	@Column(name = "root_plan_order")
	private Long rootPlanOrder;
	
	@ManyToOne
	private TaskList taskList;
	
	@NotNull
	@ManyToOne
	private RootPlanHeader rootPlanHeader;
	
	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "approval_status", nullable = false)
	private ApprovalStatus approvalStatus=ApprovalStatus.PENDING;
	
	@Column(name = "download_date")
	@JsonIgnore
	private LocalDateTime downloadDate;
	
	
	public RootPlanDetail() {
		super();
	}
	
	public RootPlanDetail( String pid, Company company, Long rootPlanOrder, TaskList taskList,RootPlanHeader rootPlanHeader) {
		super();
		this.pid = pid;
		this.company = company;
		this.rootPlanOrder = rootPlanOrder;
		this.taskList = taskList;
		this.rootPlanHeader =rootPlanHeader;
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

	

	public LocalDateTime getDownloadDate() {
		return downloadDate;
	}

	public void setDownloadDate(LocalDateTime downloadDate) {
		this.downloadDate = downloadDate;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public Long getRootPlanOrder() {
		return rootPlanOrder;
	}

	public void setRootPlanOrder(Long rootPlanOrder) {
		this.rootPlanOrder = rootPlanOrder;
	}

	public TaskList getTaskList() {
		return taskList;
	}

	public void setTaskList(TaskList taskList) {
		this.taskList = taskList;
	}

	public RootPlanHeader getRootPlanHeader() {
		return rootPlanHeader;
	}

	public void setRootPlanHeader(RootPlanHeader rootPlanHeader) {
		this.rootPlanHeader = rootPlanHeader;
	}

	public ApprovalStatus getApprovalStatus() {
		return approvalStatus;
	}

	public void setApprovalStatus(ApprovalStatus approvalStatus) {
		this.approvalStatus = approvalStatus;
	}

	@Override
	public String toString() {
		return "RootPlanDetail [id=" + id + ", pid=" + pid + ", company=" + company + ", rootPlanOrder=" + rootPlanOrder
				+ ", taskList=" + taskList + ", rootPlanHeader=" + rootPlanHeader + ", approvalStatus=" + approvalStatus
				+ ", downloadDate=" + downloadDate + "]";
	}

}

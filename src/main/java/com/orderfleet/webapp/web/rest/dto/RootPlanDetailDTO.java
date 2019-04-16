package com.orderfleet.webapp.web.rest.dto;



import com.orderfleet.webapp.domain.RootPlanDetail;
import com.orderfleet.webapp.domain.enums.ApprovalStatus;

public class RootPlanDetailDTO {
	
	private Long id;

	private String pid;
	
	private Long rootPlanOrder;
	
	private String taskListPid;
	
	private String taskListName;
	
	private String rootPlanHeaderPid;
	
	private String rootPlanHeaderName;
	
	private ApprovalStatus approvalStatus;
	
	private TaskListDTO taskListDTO;
	
	public RootPlanDetailDTO() {
		super();
	}


	public RootPlanDetailDTO(Long id, String userName, Long rootPlanOrder,
			String taskListPid,String taskListName,String rootPlanHeaderPid,String rootPlanHeaderName) {
		super();
		this.id = id;
		this.rootPlanOrder = rootPlanOrder;
		this.taskListPid = taskListPid;
		this.taskListName=taskListName;
		this.rootPlanHeaderPid=rootPlanHeaderPid;
		this.rootPlanHeaderName=rootPlanHeaderName;
	}
	
	public RootPlanDetailDTO(RootPlanDetail rootPlanDetail) {
		super();
		this.id = rootPlanDetail.getId();
		this.pid = rootPlanDetail.getPid();
		this.rootPlanOrder = rootPlanDetail.getRootPlanOrder();
		this.taskListPid = rootPlanDetail.getTaskList().getPid();
		this.taskListName = rootPlanDetail.getTaskList().getName();
		this.rootPlanHeaderPid=rootPlanDetail.getRootPlanHeader().getPid();
		this.rootPlanHeaderName=rootPlanDetail.getRootPlanHeader().getName();
		this.approvalStatus=rootPlanDetail.getApprovalStatus();
		this.taskListDTO=new TaskListDTO(rootPlanDetail.getTaskList());
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


	public String getTaskListPid() {
		return taskListPid;
	}


	public void setTaskListPid(String taskListPid) {
		this.taskListPid = taskListPid;
	}


	public String getTaskListName() {
		return taskListName;
	}


	public void setTaskListName(String taskListName) {
		this.taskListName = taskListName;
	}


	public String getRootPlanHeaderPid() {
		return rootPlanHeaderPid;
	}


	public void setRootPlanHeaderPid(String rootPlanHeaderPid) {
		this.rootPlanHeaderPid = rootPlanHeaderPid;
	}


	public String getRootPlanHeaderName() {
		return rootPlanHeaderName;
	}


	public void setRootPlanHeaderName(String rootPlanHeaderName) {
		this.rootPlanHeaderName = rootPlanHeaderName;
	}


	public Long getRootPlanOrder() {
		return rootPlanOrder;
	}


	public void setRootPlanOrder(Long rootPlanOrder) {
		this.rootPlanOrder = rootPlanOrder;
	}


	public ApprovalStatus getApprovalStatus() {
		return approvalStatus;
	}


	public void setApprovalStatus(ApprovalStatus approvalStatus) {
		this.approvalStatus = approvalStatus;
	}


	public TaskListDTO getTaskListDTO() {
		return taskListDTO;
	}


	public void setTaskListDTO(TaskListDTO taskListDTO) {
		this.taskListDTO = taskListDTO;
	}


	@Override
	public String toString() {
		return "RootPlanDetailDTO [id=" + id + ", pid=" + pid + ", rootPlanOrder=" + rootPlanOrder + ", taskListPid="
				+ taskListPid + ", taskListName=" + taskListName + ", rootPlanHeaderPid=" + rootPlanHeaderPid
				+ ", rootPlanHeaderName=" + rootPlanHeaderName + ", approvalStatus=" + approvalStatus + ", taskListDTO="
				+ taskListDTO + "]";
	}



}

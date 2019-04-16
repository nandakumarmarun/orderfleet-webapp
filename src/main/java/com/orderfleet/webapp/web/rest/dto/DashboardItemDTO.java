package com.orderfleet.webapp.web.rest.dto;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.orderfleet.webapp.domain.DashboardItem;
import com.orderfleet.webapp.domain.enums.DashboardItemConfigType;
import com.orderfleet.webapp.domain.enums.DashboardItemType;
import com.orderfleet.webapp.domain.enums.DocumentType;
import com.orderfleet.webapp.domain.enums.TaskPlanType;

/**
 * A DTO for the DashboardItem entity.
 * 
 * @author Muhammed Riyas T
 * @since Jan 18, 2017
 */
public class DashboardItemDTO {

	private String pid;

	@NotNull
	@Size(min = 1, max = 100)
	private String name;
	
	private int sortOrder;

	private DashboardItemType dashboardItemType;

	private TaskPlanType taskPlanType;

	private DocumentType documentType;

	private DashboardItemConfigType dashboardItemConfigType = DashboardItemConfigType.DASHBOARD;

	private Set<ActivityDTO> activities = new HashSet<>();

	private Set<DocumentDTO> documents = new HashSet<>();

	private Set<ProductGroupDTO> productGroups = new HashSet<>();
	
	private String salesTargetGroupPid;
	
	private String salesTargetBlockPid;

	private SalesTargetBlockDTO salesTargetBlock;

	public DashboardItemDTO() {
	}

	public DashboardItemDTO(DashboardItem dashboardItem) {
		super();
		this.pid = dashboardItem.getPid();
		this.name = dashboardItem.getName();
		this.sortOrder = dashboardItem.getSortOrder();
		this.dashboardItemType = dashboardItem.getDashboardItemType();
		this.documentType = dashboardItem.getDocumentType();
		this.taskPlanType = dashboardItem.getTaskPlanType();
		this.dashboardItemConfigType = dashboardItem.getDashboardItemConfigType();
		if (dashboardItem.getActivities() != null) {
			this.activities = dashboardItem.getActivities().stream().map(ActivityDTO::new).collect(Collectors.toSet());
		}
		if (dashboardItem.getDocuments() != null) {
			this.documents = dashboardItem.getDocuments().stream().map(DocumentDTO::new).collect(Collectors.toSet());
		}
		if (dashboardItem.getProductGroups() != null) {
			this.productGroups = dashboardItem.getProductGroups().stream().map(ProductGroupDTO::new)
					.collect(Collectors.toSet());
		}
		
		if (dashboardItem.getSalesTargetGroup() != null) {
			this.salesTargetGroupPid = dashboardItem.getSalesTargetGroup().getPid();
		}
		
		if (dashboardItem.getSalesTargetBlock() != null) {
			this.salesTargetBlockPid = dashboardItem.getSalesTargetBlock().getPid();
			this.salesTargetBlock = new SalesTargetBlockDTO(dashboardItem.getSalesTargetBlock());
		}
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public int getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(int sortOrder) {
		this.sortOrder = sortOrder;
	}

	public DashboardItemType getDashboardItemType() {
		return dashboardItemType;
	}

	public void setDashboardItemType(DashboardItemType dashboardItemType) {
		this.dashboardItemType = dashboardItemType;
	}

	public TaskPlanType getTaskPlanType() {
		return taskPlanType;
	}

	public void setTaskPlanType(TaskPlanType taskPlanType) {
		this.taskPlanType = taskPlanType;
	}

	public DocumentType getDocumentType() {
		return documentType;
	}

	public void setDocumentType(DocumentType documentType) {
		this.documentType = documentType;
	}

	public Set<ActivityDTO> getActivities() {
		return activities;
	}

	public void setActivities(Set<ActivityDTO> activities) {
		this.activities = activities;
	}

	public Set<DocumentDTO> getDocuments() {
		return documents;
	}

	public void setDocuments(Set<DocumentDTO> documents) {
		this.documents = documents;
	}

	public Set<ProductGroupDTO> getProductGroups() {
		return productGroups;
	}

	public void setProductGroups(Set<ProductGroupDTO> productGroups) {
		this.productGroups = productGroups;
	}

	public String getSalesTargetGroupPid() {
		return salesTargetGroupPid;
	}

	public void setSalesTargetGroupPid(String salesTargetGroupPid) {
		this.salesTargetGroupPid = salesTargetGroupPid;
	}

	public String getSalesTargetBlockPid() {
		return salesTargetBlockPid;
	}

	public void setSalesTargetBlockPid(String salesTargetBlockPid) {
		this.salesTargetBlockPid = salesTargetBlockPid;
	}

	public SalesTargetBlockDTO getSalesTargetBlock() {
		return salesTargetBlock;
	}

	public void setSalesTargetBlock(SalesTargetBlockDTO salesTargetBlock) {
		this.salesTargetBlock = salesTargetBlock;
	}

	public DashboardItemConfigType getDashboardItemConfigType() {
		return dashboardItemConfigType;
	}

	public void setDashboardItemConfigType(DashboardItemConfigType dashboardItemConfigType) {
		this.dashboardItemConfigType = dashboardItemConfigType;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		DashboardItemDTO accountTypeDTO = (DashboardItemDTO) o;

		if (!Objects.equals(pid, accountTypeDTO.pid))
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(pid);
	}

}

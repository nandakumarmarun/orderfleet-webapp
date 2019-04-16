package com.orderfleet.webapp.web.rest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A DTO for the EmployeeHierarchy entity.
 * 
 * @author Shaheer
 * @since June 02, 2016
 */
public class EmployeeHierarchyDTO {
	
	@JsonProperty("id")
    private Long employeeId;
	
	private String employeePid;

    private String employeeName;

    private Long parentId;
    
    private String parentPid;

    private String parentName;
    
    private String designationName;

    public Long getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(Long employeeId) {
		this.employeeId = employeeId;
	}

	public String getEmployeePid() {
		return employeePid;
	}

	public void setEmployeePid(String employeePid) {
		this.employeePid = employeePid;
	}

	public String getEmployeeName() {
		return employeeName;
	}

	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}

	public Long getParentId() {
		return parentId;
	}

	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}
	
	public String getParentPid() {
		return parentPid;
	}

	public void setParentPid(String parentPid) {
		this.parentPid = parentPid;
	}

	public String getParentName() {
		return parentName;
	}

	public void setParentName(String parentName) {
		this.parentName = parentName;
	}
	
	public String getDesignationName() {
		return designationName;
	}

	public void setDesignationName(String designationName) {
		this.designationName = designationName;
	}

	@Override
    public String toString() {
        return "EmployeeHierarchyDTO{" +
             "employeeId=" + employeeId +
             ", parentId=" + parentId +
             ", employeePid='" + employeePid + "'" +
             ", employeeName='" + employeeName + "'" +
             ", parentName='" + parentName + "'" +
             ", designationName='" + designationName + "'" +
             '}';
    }
}

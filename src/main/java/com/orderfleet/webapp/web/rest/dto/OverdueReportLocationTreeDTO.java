package com.orderfleet.webapp.web.rest.dto;

import java.util.ArrayList;
import java.util.List;

public class OverdueReportLocationTreeDTO {

	private String pid;
	
    private String name;
    
    private String parentPid;
    
    private List<OverdueReportLocationTreeDTO> children = new ArrayList<>();
    
    public void addChildrenItem(OverdueReportLocationTreeDTO child){
        if(!this.children.contains(child))
            this.children.add(child);
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

	public String getParentPid() {
		return parentPid;
	}

	public void setParentPid(String parentPid) {
		this.parentPid = parentPid;
	}

	public List<OverdueReportLocationTreeDTO> getChildren() {
		return children;
	}

	public void setChildren(List<OverdueReportLocationTreeDTO> children) {
		this.children = children;
	}
	
}

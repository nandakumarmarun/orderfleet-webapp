package com.orderfleet.webapp.web.rest.dto;

import java.util.List;

public class LocationHierarchyCustomDTO {
private List<LocationHierarchyDTO> selectednode;
private List<LocationHierarchyAddedDTO> addednode;

public LocationHierarchyCustomDTO() {
	super();
	// TODO Auto-generated constructor stub
}
public List<LocationHierarchyDTO> getSelectednode() {
	return selectednode;
}
public void setSelectednode(List<LocationHierarchyDTO> selectednode) {
	this.selectednode = selectednode;
}
public List<LocationHierarchyAddedDTO> getAddednode() {
	return addednode;
}
public void setAddednode(List<LocationHierarchyAddedDTO> addednode) {
	this.addednode = addednode;
}
@Override
public String toString() {
	return "LocationHierarchyCustomDTO [selectednode=" + selectednode + ", addednode=" + addednode + "]";
}

}

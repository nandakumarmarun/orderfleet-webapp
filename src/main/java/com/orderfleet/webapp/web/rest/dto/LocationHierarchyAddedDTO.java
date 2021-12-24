package com.orderfleet.webapp.web.rest.dto;

public class LocationHierarchyAddedDTO {
 private Long id;
 private String name;
 
public LocationHierarchyAddedDTO() {
	super();
	// TODO Auto-generated constructor stub
}

public Long getId() {
	return id;
}
public void setId(Long id) {
	this.id = id;
}
public String getName() {
	return name;
}
public void setName(String name) {
	this.name = name;
}

@Override
public String toString() {
	return "LocationHierarchyAddedDTO [id=" + id + ", name=" + name + "]";
}
 
}

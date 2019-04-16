package com.orderfleet.webapp.web.rest.dto;

import com.orderfleet.webapp.domain.ActivityDocument;

/**
 * A DTO for the ActivityDocument entity.
 * 
 * @author Muhammed Riyas T
 * @since Feb 21, 2017
 */
public class ActivityDocumentDTO {

	private String activityPid;

	private String activityName;

	private String documentPid;

	private String documentName;

	private boolean required;

	private int sortOrder;

	public ActivityDocumentDTO() {
		super();

	}

	public ActivityDocumentDTO(ActivityDocument activityDocument) {
		super();
		this.activityPid = activityDocument.getActivity().getPid();
		this.activityName = activityDocument.getActivity().getName();
		this.documentPid = activityDocument.getDocument().getPid();
		this.documentName = activityDocument.getDocument().getName();
		this.required = activityDocument.getRequired();
		this.sortOrder = activityDocument.getSortOrder();
	}

	public String getActivityPid() {
		return activityPid;
	}

	public void setActivityPid(String activityPid) {
		this.activityPid = activityPid;
	}

	public String getActivityName() {
		return activityName;
	}

	public void setActivityName(String activityName) {
		this.activityName = activityName;
	}

	public String getDocumentPid() {
		return documentPid;
	}

	public void setDocumentPid(String documentPid) {
		this.documentPid = documentPid;
	}

	public String getDocumentName() {
		return documentName;
	}

	public void setDocumentName(String documentName) {
		this.documentName = documentName;
	}

	public boolean getRequired() {
		return required;
	}

	public void setRequired(boolean required) {
		this.required = required;
	}

	public int getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(int sortOrder) {
		this.sortOrder = sortOrder;
	}

}

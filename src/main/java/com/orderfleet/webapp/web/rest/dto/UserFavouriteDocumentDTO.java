package com.orderfleet.webapp.web.rest.dto;

import java.time.LocalDateTime;

import com.orderfleet.webapp.domain.UserFavouriteDocument;

/**
 * A DTO for the UserFavouriteDocument entity.
 * 
 * @author Muhammed Riyas T
 * @since Novembor 01, 2016
 */
public class UserFavouriteDocumentDTO {

	private String userPid;

	private String activityPid;
	private String activityName;

	private String documentPid;
	private String documentName;
	private LocalDateTime lastModifiedDate;
	private int sortOrder;
	public UserFavouriteDocumentDTO() {
	}

	public UserFavouriteDocumentDTO(UserFavouriteDocument userFavouriteDocument) {
		super();
		this.userPid = userFavouriteDocument.getUser().getPid();
		this.activityPid = userFavouriteDocument.getActivity().getPid();
		this.activityName = userFavouriteDocument.getActivity().getName();
		this.documentPid = userFavouriteDocument.getDocument().getPid();
		this.documentName = userFavouriteDocument.getDocument().getName();
		this.lastModifiedDate = userFavouriteDocument.getLastModifiedDate();
		this.sortOrder=userFavouriteDocument.getSortOrder();
	}

	public String getUserPid() {
		return userPid;
	}

	public void setUserPid(String userPid) {
		this.userPid = userPid;
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

	public LocalDateTime getLastModifiedDate() {
		return lastModifiedDate;
	}

	public void setLastModifiedDate(LocalDateTime lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}

	public int getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(int sortOrder) {
		this.sortOrder = sortOrder;
	}

	@Override
	public String toString() {
		return "UserFavouriteDocumentDTO [userPid=" + userPid + ", activityPid=" + activityPid + ", activityName="
				+ activityName + ", documentPid=" + documentPid + ", documentName=" + documentName
				+ ", lastModifiedDate=" + lastModifiedDate + ", sortOrder=" + sortOrder + "]";
	}

	
}

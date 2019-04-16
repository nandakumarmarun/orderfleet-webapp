package com.orderfleet.webapp.web.rest.dto;

import com.orderfleet.webapp.domain.DocumentPrint;

/**
 * A DTO for the DocumentPrint entity.
 *
 * @author Sarath
 * @since Aug 12, 2017
 *
 */
public class DocumentPrintDTO {

	private String pid;
	private String userPid;
	private String userLoginName;
	private String userFirstName;
	private String userLastName;
	private String activityName;
	private String activityPid;
	private String documentName;
	private String documentPid;
	private boolean printStatus;

	public DocumentPrintDTO() {
		super();
	}

	public DocumentPrintDTO(DocumentPrint documentPrint) {
		super();
		this.pid = documentPrint.getPid();
		this.userPid = documentPrint.getUser().getPid();
		this.userFirstName = documentPrint.getUser().getFirstName();
		this.userLastName = documentPrint.getUser().getLastName();
		this.userLoginName = documentPrint.getUser().getLogin();
		this.activityName = documentPrint.getActivity().getName();
		this.activityPid = documentPrint.getActivity().getPid();
		this.documentName = documentPrint.getDocument().getName();
		this.documentPid = documentPrint.getDocument().getPid();
		this.printStatus = documentPrint.isPrintStatus();
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public String getUserPid() {
		return userPid;
	}

	public void setUserPid(String userPid) {
		this.userPid = userPid;
	}

	public String getUserLoginName() {
		return userLoginName;
	}

	public void setUserLoginName(String userLoginName) {
		this.userLoginName = userLoginName;
	}

	public String getUserFirstName() {
		return userFirstName;
	}

	public void setUserFirstName(String userFirstName) {
		this.userFirstName = userFirstName;
	}

	public String getUserLastName() {
		return userLastName;
	}

	public void setUserLastName(String userLastName) {
		this.userLastName = userLastName;
	}

	public String getActivityName() {
		return activityName;
	}

	public void setActivityName(String activityName) {
		this.activityName = activityName;
	}

	public String getActivityPid() {
		return activityPid;
	}

	public void setActivityPid(String activityPid) {
		this.activityPid = activityPid;
	}

	public String getDocumentName() {
		return documentName;
	}

	public void setDocumentName(String documentName) {
		this.documentName = documentName;
	}

	public String getDocumentPid() {
		return documentPid;
	}

	public void setDocumentPid(String documentPid) {
		this.documentPid = documentPid;
	}

	public boolean isPrintStatus() {
		return printStatus;
	}

	public void setPrintStatus(boolean printStatus) {
		this.printStatus = printStatus;
	}

	@Override
	public String toString() {
		return "DocumentPrintDTO [pid=" + pid + ", userPid=" + userPid + ", activityName=" + activityName
				+ ", activityPid=" + activityPid + ", documentName=" + documentName + ", documentPid=" + documentPid
				+ ", printStatus=" + printStatus + "]";
	}

}

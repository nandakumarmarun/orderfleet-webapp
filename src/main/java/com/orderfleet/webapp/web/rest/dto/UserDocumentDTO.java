package com.orderfleet.webapp.web.rest.dto;

import com.orderfleet.webapp.domain.UserDocument;

/**
 * A DTO for User Document.
 * 
 * @author Athul
 * @since April 12, 2018
 */

public class UserDocumentDTO {

	private String userPid;
	private String userName;
	private String documentPid;
	private String documentName;
	private boolean imageOption;
	private boolean smsOption;
	private boolean smsApienable;

	public UserDocumentDTO() {
		super();
	}

	public UserDocumentDTO(UserDocument userDocument) {
		super();
		this.userPid = userDocument.getUser().getPid();
		this.userName = userDocument.getUser().getFirstName();
		this.documentPid = userDocument.getDocument().getPid();
		this.documentName = userDocument.getDocument().getName();
		this.imageOption = userDocument.getImageOption();
		this.smsOption = userDocument.getSmsOption();
		this.smsApienable = userDocument.getDocument().getSmsApiEnable();
	
	}

	public String getUserPid() {
		return userPid;
	}

	public void setUserPid(String userPid) {
		this.userPid = userPid;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
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

	public boolean getImageOption() {
		return imageOption;
	}

	public void setImageOption(boolean imageOption) {
		this.imageOption = imageOption;
	}

	public boolean getSmsOption() {
		return smsOption;
	}

	public void setSmsOption(boolean smsOption) {
		this.smsOption = smsOption;
	}

	public boolean getSmsApienable() {
		return smsApienable;
	}

	public void setSmsApienable(boolean smsApienable) {
		this.smsApienable = smsApienable;
	}
	
	

}

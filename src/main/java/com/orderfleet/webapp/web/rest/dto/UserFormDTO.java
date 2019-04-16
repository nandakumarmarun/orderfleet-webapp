package com.orderfleet.webapp.web.rest.dto;

import com.orderfleet.webapp.domain.UserForm;

/**
 * A DTO for the UserForm entity.
 *
 * @author Sarath
 * @since Apr 19, 2017
 *
 */
public class UserFormDTO {

	private String userPid;
	private String userName;

	private String formPid;
	private String formName;

	private Integer sortOrder = 0;

	public UserFormDTO() {
		super();
	}

	public UserFormDTO(UserForm userForm) {
		super();
		this.userPid = userForm.getUser().getPid();
		this.userName = userForm.getUser().getLogin();
		this.formPid = userForm.getForm().getPid();
		this.formName = userForm.getForm().getName();
		this.sortOrder = userForm.getSortOrder();
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

	public String getFormPid() {
		return formPid;
	}

	public void setFormPid(String formPid) {
		this.formPid = formPid;
	}

	public String getFormName() {
		return formName;
	}

	public void setFormName(String formName) {
		this.formName = formName;
	}

	public Integer getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(Integer sortOrder) {
		this.sortOrder = sortOrder;
	}

	@Override
	public String toString() {
		return "UserFormDTO [userPid=" + userPid + ", userName=" + userName + ", formPid=" + formPid + ", formName="
				+ formName + ", sortOrder=" + sortOrder + "]";
	}

}

package com.orderfleet.webapp.web.rest.dto;

import com.orderfleet.webapp.domain.UserMenuItem;

/**
 * A DTO for the UserMenuItem entity.
 * 
 * @author Shaheer
 * @since December 30, 2016
 */
public class UserMenuItemDTO {

	private Long id;

	private String userPid;

	private String login;

	private Long menuItemId;

	private String menuItemLabel;

	private Integer sortOrder;
	
	public UserMenuItemDTO() {
	}
	
	public UserMenuItemDTO(UserMenuItem userMenuItem) {
		super();
		this.id = userMenuItem.getId();
		this.userPid = userMenuItem.getUser().getPid();
		this.login = userMenuItem.getUser().getLogin();
		this.menuItemId = userMenuItem.getMenuItem().getId();
		this.menuItemLabel = userMenuItem.getMenuItem().getLabel();
		this.sortOrder = userMenuItem.getSortOrder();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUserPid() {
		return userPid;
	}

	public void setUserPid(String userPid) {
		this.userPid = userPid;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public Long getMenuItemId() {
		return menuItemId;
	}

	public void setMenuItemId(Long menuItemId) {
		this.menuItemId = menuItemId;
	}

	public String getMenuItemLabel() {
		return menuItemLabel;
	}

	public void setMenuItemLabel(String menuItemLabel) {
		this.menuItemLabel = menuItemLabel;
	}

	public Integer getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(Integer sortOrder) {
		this.sortOrder = sortOrder;
	}

}

package com.orderfleet.webapp.web.rest.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.orderfleet.webapp.domain.MenuItem;

/**
 * A DTO for the MenuItem entity.
 *
 * @author Sarath
 * @since Dec 27, 2016
 */
public class MenuItemDTO {

	private Long id;

	@NotNull
	@Size(min = 1, max = 255)
	private String label;

	private String link;

	private Long parentId;

	private String parentLabel;

	private String description;

	@NotNull
	private Boolean activated = true;

	@NotNull
	private Integer sortOrder = 0;

	@NotNull
	private String iconClass = "entypo-users";

	public MenuItemDTO() {
		super();
	}

	public MenuItemDTO(MenuItem menuItem) {
		super();
		this.id = menuItem.getId();
		this.label = menuItem.getLabel();
		this.link = menuItem.getLink();
		if (menuItem.getParent() != null) {
			this.parentId = menuItem.getParent().getId();
			this.parentLabel = menuItem.getParent().getLabel();
		}
		this.description = menuItem.getDescription();
		this.sortOrder = menuItem.getSortOrder();
		this.iconClass = menuItem.getIconClass();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public Long getParentId() {
		return parentId;
	}

	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}

	public String getParentLabel() {
		return parentLabel;
	}

	public void setParentLabel(String parentLabel) {
		this.parentLabel = parentLabel;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Boolean getActivated() {
		return activated;
	}

	public void setActivated(Boolean activated) {
		this.activated = activated;
	}

	public Integer getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(Integer sortOrder) {
		this.sortOrder = sortOrder;
	}

	public String getIconClass() {
		return iconClass;
	}

	public void setIconClass(String iconClass) {
		this.iconClass = iconClass;
	}

	@Override
	public String toString() {
		return "MenuItemDTO [id=" + id + ", label=" + label + ", link=" + link + ", parentPid=" + parentId
				+ ", parentLabel=" + parentLabel + ", activated=" + activated + ", sortOrder=" + sortOrder
				+ ", description=" + description + ", iconClass=" + iconClass + "]";
	}

}

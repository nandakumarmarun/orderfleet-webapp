package com.orderfleet.webapp.web.rest.dto;

import java.util.Objects;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.orderfleet.webapp.domain.MobileMenuItemGroup;

/**
 * A DTO for the MobileMenuItemGroup entity.
 * 
 * @author Muhammed Riyas T
 * @since Feb 01, 2017
 */
public class MobileMenuItemGroupDTO {

	private String pid;

	@NotNull
	@Size(min = 1, max = 255)
	private String name;

	@Size(max = 55)
	private String alias;

	private String description;

	public MobileMenuItemGroupDTO() {
		super();
	}

	public MobileMenuItemGroupDTO(MobileMenuItemGroup mobileMenuItemGroup) {
		super();
		this.pid = mobileMenuItemGroup.getPid();
		this.name = mobileMenuItemGroup.getName();
		this.alias = mobileMenuItemGroup.getAlias();
		this.description = mobileMenuItemGroup.getDescription();
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

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		MobileMenuItemGroupDTO mobileMenuItemGroupDTO = (MobileMenuItemGroupDTO) o;

		if (!Objects.equals(pid, mobileMenuItemGroupDTO.pid))
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(pid);
	}

	@Override
	public String toString() {
		return "MobileMenuItemGroupDTO{" + ", pid='" + pid + "'" + ", name='" + name + "'" + ", alias='" + alias + "'"
				+ ", description='" + description + "'" + '}';
	}
}

package com.orderfleet.webapp.web.rest.dto;

import java.util.Objects;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * A DTO for the Bank entity.
 * 
 * @author Sarath
 * @since July 27, 2016
 */
public class SalesLedgerDTO {

	private String pid;

	@NotNull
	@Size(min = 1, max = 255)
	private String name;

	@Size(max = 55)
	private String alias;

	private String description;

	private boolean activated;

	private String salesLedgerId;

	private String salesLedgerCode;

	public boolean getActivated() {
		return activated;
	}

	public void setActivated(boolean activated) {
		this.activated = activated;
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

	public String getSalesLedgerId() {
		return salesLedgerId;
	}

	public void setSalesLedgerId(String salesLedgerId) {
		this.salesLedgerId = salesLedgerId;
	}

	public String getSalesLedgerCode() {
		return salesLedgerCode;
	}

	public void setSalesLedgerCode(String salesLedgerCode) {
		this.salesLedgerCode = salesLedgerCode;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		SalesLedgerDTO bankDTO = (SalesLedgerDTO) o;

		if (!Objects.equals(pid, bankDTO.pid))
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(pid);
	}

	@Override
	public String toString() {
		return "BankDTO [pid=" + pid + ", name=" + name + ", alias=" + alias + ", description=" + description
				+ ", activated=" + activated + "]";
	}
}
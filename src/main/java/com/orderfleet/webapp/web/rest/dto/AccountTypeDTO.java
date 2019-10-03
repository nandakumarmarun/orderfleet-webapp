package com.orderfleet.webapp.web.rest.dto;

import java.time.LocalDateTime;
import java.util.Objects;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.orderfleet.webapp.domain.AccountType;
import com.orderfleet.webapp.domain.enums.AccountNameType;
import com.orderfleet.webapp.domain.enums.ReceiverSupplierType;

/**
 * A DTO for the AccountType entity.
 * 
 * @author Muhammed Riyas T
 * @since May 14, 2016
 */
public class AccountTypeDTO {

	private String pid;

	@NotNull
	@Size(min = 1, max = 255)
	private String name;

	@Size(max = 55)
	private String alias;

	private String description;

	private Boolean activated;

	private LocalDateTime lastModifiedDate;

	private AccountNameType accountNameType;

	private ReceiverSupplierType receiverSupplierType;

	public AccountTypeDTO() {
		super();
	}

	public AccountTypeDTO(AccountType accountType) {
		super();
		this.pid = accountType.getPid();
		this.name = accountType.getName();
		this.alias = accountType.getAlias();
		this.description = accountType.getDescription();
		this.lastModifiedDate = accountType.getLastModifiedDate();
		this.activated = accountType.getActivated();
		this.accountNameType = accountType.getAccountNameType();
		this.receiverSupplierType = accountType.getReceiverSupplierType();
	}

	public Boolean getActivated() {
		return activated;
	}

	public void setActivated(Boolean activated) {
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

	public LocalDateTime getLastModifiedDate() {
		return lastModifiedDate;
	}

	public void setLastModifiedDate(LocalDateTime lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}

	public AccountNameType getAccountNameType() {
		return accountNameType;
	}

	public void setAccountNameType(AccountNameType accountNameType) {
		this.accountNameType = accountNameType;
	}

	public ReceiverSupplierType getReceiverSupplierType() {
		return receiverSupplierType;
	}

	public void setReceiverSupplierType(ReceiverSupplierType receiverSupplierType) {
		this.receiverSupplierType = receiverSupplierType;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		AccountTypeDTO accountTypeDTO = (AccountTypeDTO) o;

		if (!Objects.equals(pid, accountTypeDTO.pid))
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(pid);
	}

	@Override
	public String toString() {
		return "AccountTypeDTO{" + ", pid='" + pid + "'" + ", name='" + name + "'" + ", alias='" + alias + "'"
				+ ", description='" + description + "'" + '}';
	}
}

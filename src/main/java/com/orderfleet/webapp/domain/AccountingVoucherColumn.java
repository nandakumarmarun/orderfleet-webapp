package com.orderfleet.webapp.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * An column name used by accounting voucher static documents.
 * 
 * @author Shaheer
 * @since July 12, 2016
 */
@Entity
@Table(name = "tbl_accounting_voucher_column")
public class AccountingVoucherColumn implements Serializable {

	private static final long serialVersionUID = 1L;

	@NotNull
	@Size(min = 1, max = 150)
	@Id
	@Column(length = 150)
	private String name;

	public AccountingVoucherColumn() {
		super();
	}

	public AccountingVoucherColumn(String name) {
		super();
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		AccountingVoucherColumn authority = (AccountingVoucherColumn) o;

		if (name != null ? !name.equals(authority.name) : authority.name != null) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		return name != null ? name.hashCode() : 0;
	}

	@Override
	public String toString() {
		return "AccountingVoucherColumn{" + "name='" + name + '\'' + "}";
	}
}

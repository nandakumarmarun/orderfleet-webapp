package com.orderfleet.webapp.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * An column name used by inventory voucher static documents.
 * 
 * @author Shaheer
 * @since July 12, 2016
 */
@Entity
@Table(name = "tbl_inventory_voucher_column")
public class InventoryVoucherColumn implements Serializable {

	private static final long serialVersionUID = 1L;

	@NotNull
	@Size(min = 1, max = 150)
	@Id
	@Column(length = 150)
	private String name;

	public InventoryVoucherColumn() {
		super();
	}

	public InventoryVoucherColumn(String name) {
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

		InventoryVoucherColumn authority = (InventoryVoucherColumn) o;

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
		return "InventoryVoucherColumn{" + "name='" + name + '\'' + "}";
	}
}
